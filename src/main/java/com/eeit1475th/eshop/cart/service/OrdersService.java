package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.dto.OrderDTO;
import com.eeit1475th.eshop.cart.dto.OrderItemDTO;
import com.eeit1475th.eshop.cart.dto.OrderItemUpdateDTO;
import com.eeit1475th.eshop.cart.dto.OrderSummary;
import com.eeit1475th.eshop.cart.dto.OrderUpdateDTO;
import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.OrderItems;
import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.SelectedStore;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.cart.repository.OrderItemsRepository;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;
import com.eeit1475th.eshop.cart.repository.SelectedStoreRepository;
import com.eeit1475th.eshop.cart.repository.ShoppingCartRepository;
import com.eeit1475th.eshop.controller.CheckoutRestController;
import com.eeit1475th.eshop.coupon.CouponService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class OrdersService {

	private static final Logger logger = LogManager.getLogger(CheckoutRestController.class);

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private ShoppingCartRepository shoppingCartRepository;

	@Autowired
	private OrderItemsRepository orderItemsRepository;

	@Autowired
	private CouponService couponService;

	@Autowired
	private SelectedStoreRepository selectedStoreRepository;

	/**
	 * 將傳入的 Orders 物件保存（或更新）到資料庫中
	 */
	public void updateOrder(Orders order) {
		ordersRepository.save(order);
	}

	/**
	 * 根據使用者購物車、運送方式、選擇的城市與地區、以及優惠碼計算訂單摘要。
	 * 
	 * @param userId           使用者編號
	 * @param shippingMethod   運送方式 (例如 "711-cod", "711-no-cod", "outer-island",
	 *                         "hct" 等)
	 * @param selectedCity     使用者選擇的城市（例如 "台北市"、"新竹市"）
	 * @param selectedDistrict 使用者選擇的區域
	 * @param couponCode       優惠碼（若無可為 null 或空字串）
	 * @return 訂單摘要 OrderSummary，包含小計、運費、優惠折扣與最終合計
	 */
	public OrderSummary calculateOrderSummary(Integer userId, String shippingMethod, String selectedCity,
			String selectedDistrict, String couponCode) {
		// 取得使用者購物車
		ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
		if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
			throw new RuntimeException("購物車為空，無法創建訂單");
		}
		// 計算購物車小計：累加所有 CartItems 的價格*數量
		BigDecimal subtotal = BigDecimal.ZERO;
		for (CartItems item : shoppingCart.getCartItems()) {
			subtotal = subtotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}
		// 根據運送方式與前端選擇的城市/區域計算運費
		BigDecimal shippingFee = calculateShippingFee(shippingMethod, selectedCity, selectedDistrict);
		// 透過 CouponService 取得優惠券折扣（若找不到則折扣為0）
		BigDecimal couponDiscount = couponService.getDiscountByCouponCode(couponCode);
		if (couponDiscount == null) {
			couponDiscount = BigDecimal.ZERO;
		}
		// 計算最終合計 = 小計 + 運費 - 優惠券折扣，最低不得低於 0
		BigDecimal grandTotal = subtotal.add(shippingFee).subtract(couponDiscount);
		if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
			grandTotal = BigDecimal.ZERO;
		}
		// 組合結果 DTO
		OrderSummary summary = new OrderSummary();
		summary.setSubtotal(subtotal);
		summary.setShippingFee(shippingFee);
		summary.setCouponDiscount(couponDiscount);
		summary.setGrandTotal(grandTotal);
		return summary;
	}

	/**
	 * 根據運送方式、選擇的城市與區域計算運費。
	 */
	private BigDecimal calculateShippingFee(String shippingMethod, String selectedCity, String selectedDistrict) {
		BigDecimal fixedShippingFee = new BigDecimal("60");
		if ("711-cod".equals(shippingMethod) || "711-no-cod".equals(shippingMethod)) {
			return fixedShippingFee;
		}
		if (selectedCity == null || selectedCity.isEmpty() || selectedDistrict == null || selectedDistrict.isEmpty()) {
			return BigDecimal.ZERO;
		}
		BigDecimal fee = BigDecimal.ZERO;
		if ("outer-island".equals(shippingMethod)) {
			fee = new BigDecimal("100");
			if ("澎湖縣".equals(selectedCity)) {
				fee = new BigDecimal("120");
			} else if ("金門縣".equals(selectedCity) || "連江縣".equals(selectedCity)) {
				fee = new BigDecimal("150");
			}
		} else if ("hct".equals(shippingMethod)) {
			fee = new BigDecimal("80");
			if ("臺東縣".equals(selectedCity)) {
				fee = new BigDecimal("85");
			} else if ("花蓮縣".equals(selectedCity)) {
				fee = new BigDecimal("90");
			}
		} else {
			fee = fixedShippingFee;
		}
		return fee;
	}

	/**
	 * 根據使用者購物車內容建立訂單與訂單項目，並清空購物車。 此方法會同時計算訂單小計，但運費與優惠券折扣部分可由前端傳入後端，
	 * 或由前端計算完後傳入（但為避免惡意修改，建議後端也重新計算一遍）。
	 * 
	 * @param userId           使用者編號
	 * @param shippingMethod   運送方式
	 * @param selectedCity     運送地址的城市
	 * @param selectedDistrict 運送地址的區域
	 * @param couponCode       優惠碼（若無可為 null 或空字串）
	 * @param shippingAddress  實際運送地址（由使用者填寫或選擇）
	 * @param paymentMethod    付款方式
	 * @param recipientName    收件者名字
	 * @param recipientPhone   收件者電話
	 * @param orderRemark      訂單備註
	 */
	public Orders createOrder(HttpSession session, Integer userId, String shippingMethod, String selectedCity,
			String selectedDistrict, String couponCode, String shippingAddress, String paymentMethod,
			String recipientName, String recipientPhone, String orderRemark, String temporaryStoreId) {
		// 取得使用者購物車
		ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
		if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
			throw new RuntimeException("購物車為空，無法創建訂單");
		}

		// 計算購物車小計
		BigDecimal subtotal = BigDecimal.ZERO;
		for (CartItems item : shoppingCart.getCartItems()) {
			subtotal = subtotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}

		// 後端計算運費
		BigDecimal shippingFee = calculateShippingFee(shippingMethod, selectedCity, selectedDistrict);

		// 從 session 中取得優惠券折扣
		BigDecimal couponDiscount = BigDecimal.ZERO;
		Object sessionDiscount = session.getAttribute("couponDiscount");

		if (sessionDiscount != null) {
			if (sessionDiscount instanceof BigDecimal) {
				couponDiscount = (BigDecimal) sessionDiscount;
			} else if (sessionDiscount instanceof Number) {
				couponDiscount = new BigDecimal(sessionDiscount.toString());
			}
		} else {
			couponDiscount = couponService.getDiscountByCouponCode(couponCode);
			if (couponDiscount == null) {
				couponDiscount = BigDecimal.ZERO;
			}
		}

		BigDecimal grandTotal = subtotal.add(shippingFee).subtract(couponDiscount);
		if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
			grandTotal = BigDecimal.ZERO;
		}

		// 如果運送方式不是 7-11 取貨，則把城市、區域與使用者填寫的地址合併起來
		if (!"711-cod".equalsIgnoreCase(shippingMethod) && !"711-no-cod".equalsIgnoreCase(shippingMethod)) {
			// 確保城市與區域有資料
			if (selectedCity != null && !selectedCity.isEmpty() && selectedDistrict != null
					&& !selectedDistrict.isEmpty()) {
				// 可根據需求選擇合併的格式，城市 + " " + 區域 + " " + 原始地址
				shippingAddress = selectedCity + " " + selectedDistrict + " " + shippingAddress;
			}
		}

		// 若運送方式為 711-cod 或 711-no-cod，從資料庫取得 SelectedStore
		SelectedStore selectedStore = null;
		if ("711-cod".equals(shippingMethod) || "711-no-cod".equals(shippingMethod)) {
			if (temporaryStoreId != null && !temporaryStoreId.isEmpty()) {
				try {
					Integer tempStoreIdInt = Integer.valueOf(temporaryStoreId);
					selectedStore = selectedStoreRepository.findById(tempStoreIdInt)
							.orElseThrow(() -> new RuntimeException("選擇的門市資料無效"));
					// 組合門市資訊作為運送地址
					shippingAddress = "門市店號：" + selectedStore.getCVSStoreId() + "，地址：" + selectedStore.getCVSAddress();
				} catch (NumberFormatException e) {
					throw new RuntimeException("門市資料編號格式錯誤");
				}
			} else {
				throw new RuntimeException("請選擇正確的 7-11 門市");
			}
		}

		// 創建訂單
		Orders order = new Orders();
		order.setUsers(shoppingCart.getUsers());
		order.setTotalAmount(grandTotal);
		order.setPaymentMethod(paymentMethod);
		order.setPaymentStatus(PaymentStatus.待處理);
		order.setShippingMethod(shippingMethod);
		order.setShippingStatus(ShippingStatus.處理中);
		order.setShippingAddress(shippingAddress);
		order.setRecipientName(recipientName);
		order.setRecipientPhone(recipientPhone);
		order.setOrderRemark(orderRemark);
		order.setShippingFee(shippingFee);
		order.setCouponDiscount(couponDiscount);

		// 假設 Orders 物件中有 selectedStoreId 欄位，若有取得門市資訊則儲存之
		if ("711-cod".equals(shippingMethod) || "711-no-cod".equals(shippingMethod)) {
			order.setSelectedStore(selectedStore);
		}

		ordersRepository.save(order);

		// 建立訂單項目：將購物車中的商品轉換為訂單項目
		List<OrderItems> orderItemsList = new ArrayList<>();
		for (CartItems cartItem : shoppingCart.getCartItems()) {
			OrderItems orderItem = new OrderItems();
			orderItem.setOrders(order);
			orderItem.setProducts(cartItem.getProducts());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());
			orderItemsRepository.save(orderItem);
			orderItemsList.add(orderItem);
		}
		order.setOrderItems(orderItemsList);

		// 清空購物車
		shoppingCart.getCartItems().clear();
		shoppingCartRepository.save(shoppingCart);

		return order;
	}

	/**
	 * 從購物車建立訂單：假設前端傳入必要參數，這邊可以根據實際情況調整參數的來源， 例如從 HttpSession 取得 userId 或其他資料。
	 */
	public Orders createOrderFromCart(HttpSession session, HttpServletRequest request) {
		// 從 session 中取得使用者編號（請確保前端已將 userId 存入 session）
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			throw new RuntimeException("User ID not found in session");
		}

		// 從 request 中取得前端傳入的參數
		String shippingMethod = request.getParameter("shippingMethod");
		String selectedCity = request.getParameter("selectedCity");
		String selectedDistrict = request.getParameter("selectedDistrict");
		String couponCode = request.getParameter("couponCode");
		String shippingAddress = request.getParameter("shippingAddress");
		String paymentMethod = request.getParameter("paymentMethod");
		String recipientName = request.getParameter("recipientName");
		String recipientPhone = request.getParameter("recipientPhone");
		String orderRemark = request.getParameter("orderRemark");

		// 從 session 取得 paymentMethod
		if (paymentMethod == null || paymentMethod.isEmpty()) {
			paymentMethod = (String) session.getAttribute("paymentMethod");
			if (paymentMethod == null || paymentMethod.isEmpty()) {
				paymentMethod = "credit";
			}
		}

		// 從 session 取得 shippingMethod
		if (shippingMethod == null || shippingMethod.isEmpty()) {
			shippingMethod = (String) session.getAttribute("shippingMethod");
			if (shippingMethod == null || shippingMethod.isEmpty()) {
				shippingMethod = "711-cod";
			}
		}

		// 如果前端沒有傳入 selectedCity，就從 session 取得
		if (selectedCity == null || selectedCity.isEmpty()) {
			selectedCity = (String) session.getAttribute("selectedCity");
			if (selectedCity == null) {
				selectedCity = "";
			}
		}

		// 如果前端沒有傳入 selectedDistrict，就從 session 取得
		if (selectedDistrict == null || selectedDistrict.isEmpty()) {
			selectedDistrict = (String) session.getAttribute("selectedDistrict");
			if (selectedDistrict == null) {
				selectedDistrict = "";
			}
		}

		// 對其他參數加上預設值
		if (couponCode == null) {
			couponCode = "";
		}
		if (shippingAddress == null) {
			shippingAddress = "";
		}
		if (recipientName == null) {
			recipientName = "";
		}
		if (recipientPhone == null) {
			recipientPhone = "";
		}
		if (orderRemark == null) {
			orderRemark = "";
		}

		// 呼叫內部方法建立訂單
		return createOrder(session, userId, shippingMethod, selectedCity, selectedDistrict, couponCode, shippingAddress,
				paymentMethod, recipientName, recipientPhone, orderRemark, request.getParameter("temporaryStoreId"));
	}

	/**
	 * 透過訂單編號查詢訂單
	 */
	public Orders getOrderById(Integer orderId) {
		return ordersRepository.findById(orderId).orElse(null);
	}

	/**
	 * 查詢訂單詳情，將 Orders 與相關訂單項目轉換為 OrderDTO 回傳
	 * 
	 * @param orderId 訂單編號
	 * @return OrderDTO 訂單資料
	 */
	public OrderDTO getOrderDetails(Integer orderId) {
		Orders order = ordersRepository.findByIdWithItems(orderId);
		if (order == null) {
			throw new RuntimeException("訂單未找到");
		}

		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(order.getOrderId());
		orderDTO.setOrderDate(order.getOrderDate());
		orderDTO.setTotalAmount(order.getTotalAmount());
		orderDTO.setPaymentStatus(order.getPaymentStatus().name());
		orderDTO.setShipmentStatus(order.getShippingStatus().name());
		orderDTO.setShippingFee(order.getShippingFee());
		orderDTO.setCouponDiscount(order.getCouponDiscount());
		orderDTO.setMerchantTradeNo(order.getMerchantTradeNo());

		// 轉換訂單項目
		List<OrderItemDTO> orderItemDTOList = order.getOrderItems().stream().map(oi -> {
			OrderItemDTO dto = new OrderItemDTO();
			dto.setProductId(oi.getProducts().getProductId());
			dto.setProductName(oi.getProducts().getProductName());
			dto.setPrice(oi.getPrice());
			dto.setQuantity(oi.getQuantity());
			dto.setSubTotal(oi.getPrice().multiply(new BigDecimal(oi.getQuantity())));
//			dto.setImageUrl(oi.getProducts().getImageUrl());

			String imageUrl = oi.getProducts().getImageUrl();
			if (imageUrl != null && !imageUrl.startsWith("/")) {
				imageUrl = "/" + imageUrl;
			}
			dto.setImageUrl(imageUrl);

			return dto;
		}).collect(Collectors.toList());
		orderDTO.setOrderItems(orderItemDTOList);

		return orderDTO;
	}

	/**
	 * 更新訂單狀態（付款、發貨等）
	 * 
	 * @param merchantTradeNo 訂單編號
	 * @param status          要更新的狀態，例："paid" 或 "shipped"
	 */
	public void updateOrderStatusByMerchantTradeNo(String merchantTradeNo, String status) {
		if (merchantTradeNo == null) {
			throw new IllegalArgumentException("MerchantTradeNo must not be null");
		}
		// 去除多餘空白
		final String trimmedMerchantTradeNo = merchantTradeNo.trim();
		logger.info("收到更新請求，訂單號碼: '{}'", merchantTradeNo);

		if (!isValidMerchantTradeNo(merchantTradeNo)) {
			throw new IllegalArgumentException("訂單號碼格式錯誤: " + merchantTradeNo);
		}

		Orders order = ordersRepository.findByMerchantTradeNo(merchantTradeNo)
				.orElseThrow(() -> new RuntimeException("Order not found，merchantTradeNo=" + merchantTradeNo));
		logger.info("找到訂單，訂單編號: {}", order.getOrderId());

		if (status == null) {
			throw new IllegalArgumentException("狀態不能為 null");
		}
		status = status.trim();

		if ("paid".equalsIgnoreCase(status) || "已付款".equals(status)) {
			order.setPaymentStatus(PaymentStatus.已付款);
		} else if ("shipped".equalsIgnoreCase(status) || "已出貨".equals(status)) {
			order.setShippingStatus(ShippingStatus.已出貨);
		} else {
			throw new IllegalArgumentException("不支援的狀態: " + status);
		}

		ordersRepository.save(order);
		logger.info("訂單 {} 狀態更新為 {}", order.getOrderId(), status);
	}

	public boolean isValidMerchantTradeNo(String merchantTradeNo) {
		// 假設訂單號必須是 20 位數字
		return merchantTradeNo != null && merchantTradeNo.matches("\\d{20}");
	}

	/**
	 * 取得指定使用者的所有訂單（不包含訂單明細，可視需求擴充）
	 * 
	 * @param userId 使用者編號
	 * @return List<OrderDTO> 該使用者所有訂單
	 */
	public List<OrderDTO> getOrdersByUser(Integer userId) {
		List<Orders> orders = ordersRepository.findByUsers_UserId(userId);

		return orders.stream().map(order -> {
			OrderDTO dto = new OrderDTO();
			dto.setOrderId(order.getOrderId());
			dto.setOrderDate(order.getOrderDate());
			dto.setTotalAmount(order.getTotalAmount());
			dto.setPaymentStatus(order.getPaymentStatus().toString());
			dto.setShipmentStatus(order.getShippingStatus().toString());
			dto.setShippingFee(order.getShippingFee());
			dto.setCouponDiscount(order.getCouponDiscount());
			dto.setMerchantTradeNo(order.getMerchantTradeNo());

			List<OrderItemDTO> orderItemDTOList = order.getOrderItems().stream().map(oi -> {
				OrderItemDTO itemDTO = new OrderItemDTO();
				itemDTO.setProductId(oi.getProducts().getProductId());
				itemDTO.setProductName(oi.getProducts().getProductName());
				itemDTO.setPrice(oi.getPrice());
				itemDTO.setQuantity(oi.getQuantity());
				itemDTO.setSubTotal(oi.getPrice().multiply(new BigDecimal(oi.getQuantity())));
				itemDTO.setImageUrl(oi.getProducts().getImageUrl());
				return itemDTO;
			}).collect(Collectors.toList());
			dto.setOrderItems(orderItemDTOList);

			return dto;
		}).collect(Collectors.toList());
	}

	public Page<OrderDTO> getOrdersByUser(Integer userId, int page, int size) {
		// 設定排序：按 orderDate 降冪排序（最新在前）
		Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
		Page<Orders> ordersPage = ordersRepository.findByUsers_UserId(userId, pageable);

		// 將 Orders 轉換為 DTO
		return ordersPage.map(order -> {
			OrderDTO dto = new OrderDTO();
			dto.setOrderId(order.getOrderId());
			dto.setOrderDate(order.getOrderDate());
			dto.setTotalAmount(order.getTotalAmount());
			dto.setPaymentStatus(order.getPaymentStatus().toString());
			dto.setShipmentStatus(order.getShippingStatus().toString());
			dto.setShippingFee(order.getShippingFee());
			dto.setCouponDiscount(order.getCouponDiscount());
			dto.setMerchantTradeNo(order.getMerchantTradeNo());

			List<OrderItemDTO> orderItemDTOList = order.getOrderItems().stream().map(oi -> {
				OrderItemDTO itemDTO = new OrderItemDTO();
				itemDTO.setProductId(oi.getProducts().getProductId());
				itemDTO.setProductName(oi.getProducts().getProductName());
				itemDTO.setImageUrl(oi.getProducts().getImageUrl());
				itemDTO.setPrice(oi.getPrice());
				itemDTO.setQuantity(oi.getQuantity());
				itemDTO.setSubTotal(oi.getPrice().multiply(new BigDecimal(oi.getQuantity())));
				return itemDTO;
			}).collect(Collectors.toList());
			dto.setOrderItems(orderItemDTOList);
			return dto;
		});
	}

	public OrderSummary calculateOrderSummaryForCartItems(List<CartItemsDTO> cartItems, String shippingMethod,
			String selectedCity, String selectedDistrict, String couponCode) {

		OrderSummary summary = new OrderSummary();

		// 1. 計算購物車小計：累加所有 CartItemsDTO 的價格 * 數量
		BigDecimal subtotal = BigDecimal.ZERO;
		for (CartItemsDTO item : cartItems) {
			// 假設 getPrice() 回傳 BigDecimal，getQuantity() 回傳 int
			subtotal = subtotal.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}
		summary.setSubtotal(subtotal);

		// 2. 計算運費，這裡直接使用原有的 calculateShippingFee 方法
		BigDecimal shippingFee = calculateShippingFee(shippingMethod, selectedCity, selectedDistrict);
		summary.setShippingFee(shippingFee);

		// 3. 根據優惠碼取得優惠折扣，使用 CouponService 的邏輯
		BigDecimal couponDiscount = couponService.getDiscountByCouponCode(couponCode);
		if (couponDiscount == null) {
			couponDiscount = BigDecimal.ZERO;
		}
		summary.setCouponDiscount(couponDiscount);

		// 4. 計算最終合計：小計 + 運費 - 優惠折扣（最低不得低於 0）
		BigDecimal grandTotal = subtotal.add(shippingFee).subtract(couponDiscount);
		if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
			grandTotal = BigDecimal.ZERO;
		}
		summary.setGrandTotal(grandTotal);

		return summary;
	}

	// 刪除訂單
	public void deleteOrder(Integer orderId) {
		ordersRepository.deleteById(orderId);
	}

	// 取得所有訂單（管理者專用）
	public Page<Orders> getAllOrders(PageRequest pageRequest) {
		return ordersRepository.findAll(pageRequest);
	}

	// 搜尋訂單（管理者專用）
	public Page<Orders> searchOrders(String merchantTradeNo, String orderIdStr, String paymentStatus,
			String shippingStatus, Pageable pageable) {

		// 如果 merchantTradeNo 為 null，轉為空字串（對於模糊搜尋）
		if (merchantTradeNo == null) {
			merchantTradeNo = "";
		}

		// 轉換付款狀態與配送狀態為 Enum（若失敗或空字串則設定為 null）
		PaymentStatus paymentStatusEnum = null;
		if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
			try {
				paymentStatusEnum = PaymentStatus.valueOf(paymentStatus.trim());
			} catch (IllegalArgumentException e) {
				logger.warn("無效的付款狀態: {}", paymentStatus);
			}
		}
		ShippingStatus shippingStatusEnum = null;
		if (shippingStatus != null && !shippingStatus.trim().isEmpty()) {
			try {
				shippingStatusEnum = ShippingStatus.valueOf(shippingStatus.trim());
			} catch (IllegalArgumentException e) {
				logger.warn("無效的配送狀態: {}", shippingStatus);
			}
		}

		// 如果 orderIdStr 存在且能轉換成 Integer，使用包含 orderId 條件的查詢
		if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
			try {
				Integer orderId = Integer.valueOf(orderIdStr.trim());
				return ordersRepository.searchOrdersWithOrderId(orderId, merchantTradeNo, paymentStatusEnum,
						shippingStatusEnum, pageable);
			} catch (NumberFormatException e) {
				// 若轉換失敗，則忽略 orderId 條件
				logger.warn("orderId 格式錯誤: {}", orderIdStr);
			}
		}
		// 若 orderId 條件不存在，則使用不包含 orderId 條件的查詢
		return ordersRepository.searchOrders(merchantTradeNo, paymentStatusEnum, shippingStatusEnum, pageable);
	}
	
	public void updateOrderItems(Orders orders, OrderUpdateDTO updateDto) {
	    if (updateDto.getItems() != null) {
	        // 將更新資料轉為 map: productId -> newQuantity
	        Map<Integer, Integer> updateMap = updateDto.getItems().stream()
	            .collect(Collectors.toMap(OrderItemUpdateDTO::getProductId, OrderItemUpdateDTO::getQuantity));
	        
	        // 取得現有訂單商品列表
	        List<OrderItems> existingItems = orders.getOrderItems();
	        
	        // 使用 iterator 逐筆處理
	        Iterator<OrderItems> iterator = existingItems.iterator();
	        while (iterator.hasNext()) {
	            OrderItems orderItem = iterator.next();
	            Integer pid = orderItem.getProducts().getProductId();
	            if (updateMap.containsKey(pid)) {
	                // 更新數量
	                orderItem.setQuantity(updateMap.get(pid));
	                // 表示此項已處理，從 map 中移除
	                updateMap.remove(pid);
	            } else {
	                // 若不在更新清單中，則表示該商品應被刪除
	                iterator.remove();
	                // 若配置了 CascadeType.REMOVE，則可讓刪除動作自動級聯；若未配置，
	                // 可考慮調用此處的 repository.delete(orderItem)，或讓 updateOrder() 方法同步更新
	                // orderItemsRepository.delete(orderItem);
	            }
	        }
	        // 若 updateMap 還有剩餘的，表示更新資料中包含新項目
	    }
	}
}
