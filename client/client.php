<?php
  if (count($argv) > 2) {
    $url = $argv[1];
    $file = $argv[2];
  }
  else {
    $url = "http://127.0.0.1:8081/validate";
    $file = $argv[1];
  }

  $fc = file_get_contents($file);
  $dd = base64_encode($fc);

  $v = [ 'data' => $dd ];

  $curl = curl_init($url);

  curl_setopt($curl, CURLOPT_POST, true);
  curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($v));
  curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

  $res = curl_exec($curl);
  curl_close($curl);

  echo $res . "\n";
?>
