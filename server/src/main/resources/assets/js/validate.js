function  formatDate(d) {
    return new Date(d).toLocaleDateString(undefined, {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
}

function setup_dnd() {
  jQuery('#file-label').on('drag dragstart dragend dragover dragenter dragleave drop', (e) => {
      e.preventDefault();
      e.stopPropagation();
  })
  .on('dragover dragenter', () => {
    jQuery('#file-label').text("Rilascia qui il file da controllare");
  })
  .on('drop', (e) => {
      document.getElementById('file-loader').files =
        e.originalEvent.dataTransfer.files;
      validate_file();
  })
}

function validate_file() {
  const input = document.getElementById("file-loader");

  if (input.files.length == 0) {
    console.error("No file selected");
    jQuery("#response").html("");
    jQuery("#file-label").html("");
    return;
  }

  const file  = input.files[0];

  jQuery('#file-label').html(`<strong>File:</strong> ${file.name}`);

  let reader = new FileReader();

  reader.addEventListener('loadend', (event) => {
    var data = reader.result;
    var encoded_data = btoa(data);

    var payload = JSON.stringify({
      data: encoded_data
    });

    jQuery.post(window.location.href, payload, (res) => {
      var response = jQuery("<ul class='signatures'>")

      if (res.length == 0) {
        jQuery(response).append('Nessuna firma trovata nel documento');
      }

      jQuery(res).each((idx, sig) => {
        jQuery(response).append(`<li class="signature">
          <strong>Nome:</strong> ${sig.name}<br>
          <strong>Data:</strong> ${formatDate(sig.date)}<br>
          <strong>DN:</strong> ${sig.DN}<br>
          <strong>DN di chi ha emesso il certificato:</strong> ${sig.issuerDN}<br>
          <strong>Valido da:</strong> ${formatDate(sig.notBefore)}<br>
          <strong>Valido fino a:</strong> ${formatDate(sig.notAfter)}
        </li>`);
      });

      jQuery('#response').html(response);
    }, 'json');
  });

  reader.readAsBinaryString(file);
}

jQuery(document).ready(() => {
  setup_dnd();
})
