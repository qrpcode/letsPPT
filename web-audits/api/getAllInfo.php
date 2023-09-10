<?php
header('Content-Type:application/json; charset=utf-8');

require_once 'dao.php';

echo json_encode(findAllNotSeal());

?>