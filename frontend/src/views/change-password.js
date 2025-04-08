// 修改密码
async function changePassword() {
    const oldPassword = document.getElementById('oldPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // 验证新密码和确认密码是否匹配
    if (newPassword !== confirmPassword) {
        alert('新密碼和確認密碼不匹配');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/users/verify-and-update-password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                code: oldPassword, // 使用旧密码作为验证码
                newPassword: newPassword
            })
        });

        const data = await response.json();
        if (data.success) {
            alert('密碼修改成功');
            window.location.href = '/profile';
        } else {
            alert(data.message || '密碼修改失敗');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('密碼修改失敗，請稍後再試');
    }
} 