<?php
require_once 'dao.php';

if (!isset($_POST["great"]) || !isset($_POST["bad"]) || !isset($_POST["uids"])) {
    echo 'error';
    exit;
}

updateAllCheck($_POST["uids"]);
updateGreatCheck($_POST["great"]);
updateBadCheck($_POST["bad"]);
echo 'ok';

?>