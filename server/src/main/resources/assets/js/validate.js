var app = null;

function  formatDate(d) {
    return new Date(d).toLocaleDateString(undefined, {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
}

async function readFile(filename) {
  return new Promise((res, rej) => {
    let reader = new FileReader();
    reader.onload = () => {
      res(reader.result);
    };
    reader.onerror = rej;
    reader.readAsBinaryString(filename);
  });
}

async function validateFile() {
  const input = document.getElementById("file-loader");

  if (input.files.length == 0) {
    this.message = "No file selected";
    return;
  }

  const file  = input.files[0];
  this.fileUploadMessage = `Uploading: ${file.name} ...`;

  let data = await readFile(file);

  let res = await fetch(window.location.href, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      data: btoa(data)
    })
  });

  let signatures = {
    name: file.name,
    signatures: await res.json()
  };

  this.validatedFiles.push(signatures);
  this.fileUploadMessage = "Selezionare un file da caricare";
}

document.addEventListener("DOMContentLoaded", function(e) {
  app = new Vue({
    el: '#app',
    data: {
      fileUploadMessage: null,
      message: null,
      validatedFiles: []
    },
    methods: {
      validateFile: validateFile,
      formatDate: formatDate
    }
  });

  app.fileUploadMessage = "Selezionare un file da caricare";
});
