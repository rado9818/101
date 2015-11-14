<?php


$response = array();

 
require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$result = mysql_query("SELECT * FROM events") or die(mysql_error());

if (mysql_num_rows($result) > 0) {

    $response["events"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $itEvents = array();
        $itEvents["id"] = $row["id"];
        $itEvents["name"] = $row["name"];
        $itEvents["latitude"] = $row["latitude"];
        $itEvents["longitude"] = $row["longitude"];
        $itEvents["description"] = $row["description"];
        $itEvents["lookingForLecturers"] = $row["lookingForLecturers"];


        array_push($response["events"], $itEvents);
    }
    $response["success"] = 1;

    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Not found";

    echo json_encode($response);
}
?>
