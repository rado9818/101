<?php


$response = array();

if (isset($_POST['name']) && isset($_POST['latitude']) && isset($_POST['longitude']) && isset($_POST['description']) && isset($_POST['lecturers'])) {
    
    $name = $_POST['name'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $description = $_POST['description'];
    $lecturers = $_POST['lecturers'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    $result = mysql_query("INSERT INTO events (name, latitude, longitude, description, lookingForLecturers) VALUES('$name', '$latitude', '$longitude', '$description', '$lecturers')");

    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Successfully created.";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    echo json_encode($response);
}
?>