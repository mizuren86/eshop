// 检查是否在登录页面
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        console.log('開始登入流程...');
        console.log('用戶名:', username);

        try {
            console.log('發送登入請求...');
            const response = await fetch('/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                }),
                credentials: 'include'
            });

            console.log('收到服務器響應:', response.status);
            const data = await response.json();
            console.log('響應數據:', data);

            if (data.success) {
                console.log('登入成功！');
                console.log('Token:', data.data.token);
                console.log('用戶信息:', data.data.user);

                // 登入成功，保存 token 和用户信息
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('user', JSON.stringify(data.data.user));

                console.log('已保存到localStorage:');
                console.log('Token:', localStorage.getItem('token'));
                console.log('User:', localStorage.getItem('user'));
                console.log('Cookies:', document.cookie);

                // 验证会话状态
                try {
                    const sessionResponse = await fetch('/api/users/current', {
                        credentials: 'include'
                    });
                    const sessionData = await sessionResponse.json();
                    console.log('會話狀態:', sessionData);
                } catch (error) {
                    console.error('驗證會話狀態失敗:', error);
                }

                // 重定向到首页
                window.location.href = '/';
            } else {
                console.error('登入失敗:', data.message);
                // 显示错误信息
                const errorMessage = document.getElementById('errorMessage');
                errorMessage.textContent = data.message || '登入失败';
                errorMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('登入過程發生錯誤:', error);
            const errorMessage = document.getElementById('errorMessage');
            errorMessage.textContent = '登入过程中发生错误';
            errorMessage.style.display = 'block';
        }
    });
}

// 更新导航栏显示
function updateNavbar(isLoggedIn, user) {
    console.log('更新導航欄狀態:', isLoggedIn);
    console.log('用戶信息:', user);

    const userDropdown = document.querySelector('.nav-item.dropdown .dropdown-menu');
    if (userDropdown) {
        if (isLoggedIn) {
            console.log('設置已登入狀態的導航欄');
            userDropdown.innerHTML = `
                <a href="/shop-profile" class="dropdown-item">個人資料</a>
                <a href="/shop-order" class="dropdown-item">訂單記錄</a>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item" onclick="logout()">登出</a>
            `;
        } else {
            console.log('設置未登入狀態的導航欄');
            userDropdown.innerHTML = `
                <a href="/login" class="dropdown-item">登入</a>
                <a href="/register" class="dropdown-item">註冊</a>
            `;
        }
    }
}

// 页面加载时检查登录状态
document.addEventListener('DOMContentLoaded', async function () {
    console.log('頁面加載完成，檢查登入狀態...');
    console.log('Cookies:', document.cookie);
    console.log('localStorage中的用戶信息:', localStorage.getItem('user'));

    // 首先检查localStorage中的用户信息
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
        console.log('發現已保存的用戶信息');
        updateNavbar(true, JSON.parse(storedUser));
    }

    try {
        // 检查会话状态
        console.log('正在檢查服務器會話狀態...');
        const response = await fetch('/api/users/current', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            console.log('服務器響應狀態:', response.status);
            // 如果服务器返回错误，清除本地存储
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            updateNavbar(false);
            return;
        }

        const data = await response.json();
        console.log('會話狀態:', data);

        if (data && Object.keys(data).length > 0) {
            console.log('服務器確認用戶已登入');
            localStorage.setItem('user', JSON.stringify(data));
            updateNavbar(true, data);
        } else {
            console.log('服務器確認用戶未登入');
            // 如果服务器显示未登录，但localStorage有数据，清除localStorage
            if (storedUser) {
                console.log('清除過期的localStorage數據');
                localStorage.removeItem('user');
                localStorage.removeItem('token');
                updateNavbar(false);
            }
        }
    } catch (error) {
        console.error('檢查登入狀態失敗:', error);
        // 发生错误时，如果localStorage有数据，保持当前状态
        if (!storedUser) {
            updateNavbar(false);
        }
    }
});

async function checkSession() {
    try {
        console.log('正在檢查服務器會話狀態...');
        const response = await fetch('/api/users/current', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            console.log('會話檢查失敗:', response.status);
            return;
        }

        const data = await response.json();
        console.log('會話狀態:', data);

        if (data && Object.keys(data).length > 0) {
            console.log('服務器確認用戶已登入');
            localStorage.setItem('user', JSON.stringify(data));
            updateNavigationBar(true);
        } else {
            console.log('服務器確認用戶未登入');
            localStorage.removeItem('user');
            updateNavigationBar(false);
        }
    } catch (error) {
        console.error('檢查會話時發生錯誤:', error);
        // 发生错误时，保持当前状态不变
        const currentUser = localStorage.getItem('user');
        updateNavigationBar(!!currentUser);
    }
} 