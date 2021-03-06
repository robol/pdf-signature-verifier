var app = null;

const PSV_MESSAGES = {
  "PSV_UPLOAD_FILE": "Selezionare uno o più file da caricare, o  trascinarli su quest'area.",
  "PSV_DRAGGING_FILE": "Rilasciare qui il file da caricare...",
  "PSV_NO_FILE": "Nessun file selezionato."
};

async function onFileDrop(evt) {
  var files = evt.dataTransfer.files;
  app.dragging = false;

  Array.from(files).forEach(async (f) => {
    await app.validateFile(f);
  });

  evt.preventDefault();
}

function onFileDragOver(evt) {
  app.fileUploadMessage = PSV_MESSAGES["PSV_DRAGGING_FILE"];
  app.dragging = true;
  evt.preventDefault();
}

function onFileDragLeave(evt) {
  app.fileUploadMessage = PSV_MESSAGES["PSV_UPLOAD_FILE"];
  app.dragging = false;
  evt.preventDefault();
}

function onFileDragEnd(evt) {
  app.dragging = false;
}

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

async function validateFileFromInput() {
  const input = document.getElementById("file-loader");

  if (input.files.length == 0) {
    this.message = "No file selected";
    return;
  }

  Array.from(input.files).forEach(async (f) => {
    await app.validateFile(f);
  });
}

async function validateFile(file) {
  this.fileUploadMessage = `Uploading: ${file.name} ...`;

  let data = await readFile(file);

  let res = await fetch('./validate', {
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

  app.validatedFiles.push(signatures);
  app.fileUploadMessage = PSV_MESSAGES["PSV_UPLOAD_FILE"];
}

async function removeFile(obj) {
  this.validatedFiles = this.validatedFiles.filter((i) => i !== obj);
}

document.addEventListener("DOMContentLoaded", function(e) {
  app = new Vue({
    el: '#app',
    data: {
      fileUploadMessage: null,
      message: null,
      validatedFiles: [],
      dragging: false
    },
    methods: {
      validateFile: validateFile,
      formatDate: formatDate,
      removeFile: removeFile
    }
  });

  app.fileUploadMessage = PSV_MESSAGES["PSV_UPLOAD_FILE"];
});
