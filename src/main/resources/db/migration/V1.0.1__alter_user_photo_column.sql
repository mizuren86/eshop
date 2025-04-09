-- 首先创建一个临时列
ALTER TABLE users ADD user_photo_new VARCHAR(255);

-- 将现有数据复制到新列
UPDATE users SET user_photo_new = CAST(user_photo AS VARCHAR(255));

-- 删除旧列
ALTER TABLE users DROP COLUMN user_photo;

-- 重命名新列
EXEC sp_rename 'users.user_photo_new', 'user_photo', 'COLUMN'; 