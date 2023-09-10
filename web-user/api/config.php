<?php
    $mysqli_server_name='localhost';
    $mysqli_username="root"; 
    $mysqli_password="root";
    $mysqli_database="mubangou"; 
    $config = mysqli_connect($mysqli_server_name,$mysqli_username,$mysqli_password,$mysqli_database);
    $GLOBALS['config'] = $config;
    mysqli_set_charset($config, 'utf8');
    if (mysqli_connect_errno($config)) 
    { 
        echo mysqli_connect_error(); 
    }
?>