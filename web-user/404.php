<?php
require_once 'api/php_tool.php';
header('HTTP/1.1 404 Not Found');
header("status: 404 Not Found");
?>

<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <link rel="shortcut icon" href="../favicon.ico">
    <title>404-模板狗</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
    <div style="max-width: 600px;height: auto;width: 100%;margin: 0 auto;margin-top: 50px;">
        <img src="<?php echo getHost()?>img/404.png" style="max-width: 600px;height: auto;width: 100%;" alt="404">
        <div style="text-align: center;font-size: 30px;font-weight: bold;color: #333;margin-top: 30px;margin-bottom: 30px;">页面消失了，不如回首页看看</div>
        <a href="<?php echo getHost()?>" style="display: block;width:300px;text-align: center;height: 40px;line-height: 40px;color: #FFFFFF;border-radius: 8px;background-color: #0092F7;margin: 0 auto;text-decoration:none">回到首页</a>
    </div>
</body>
</html>