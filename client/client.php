<?php
  $fc = file_get_contents($argv[1]);
  $dd = base64_encode($fc);

  $v = [ 'data' => $dd ];

  $curl = curl_init('http://127.0.0.1:8081/validate');

  curl_setopt($curl, CURLOPT_POST, true);
  curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($v));
  curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

  $res = curl_exec($curl);
  curl_close($curl);

  echo $res . "\n";
?>
