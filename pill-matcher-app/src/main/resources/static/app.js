let shouldFaceUser = false;

const video = document.getElementById("video");
let toggleCameraButton = document.querySelector("#toggle-camera-button");
let on_stream_video = document.querySelector("#camera-stream");
let spinner = document.querySelector("#spinner");

let constraints = { audio: false, video: true };

let supports = navigator.mediaDevices.getSupportedConstraints();
if (supports["facingMode"] === true) {
  toggleCameraButton.disabled = false;
}

let stream = null;

function streamVideoFeed() {
  constraints.video = {
    width: {
      min: 600,
      ideal: 600,
      max: 600,
    },
    height: {
      min: 600,
      ideal: 600,
      max: 600,
    },
    facingMode: shouldFaceUser ? "user" : "environment",
  };

  navigator.mediaDevices
    .getUserMedia(constraints)
    .then(function (mediaStream) {
      stream = mediaStream;
      on_stream_video.srcObject = stream;
      on_stream_video.play();
    })
    .catch(function (err) {
      console.log(err);
    });
}

streamVideoFeed();

toggleCameraButton.addEventListener("click", function () {
  if (stream == null) return;

  stream.getTracks().forEach((t) => {
    t.stop();
  });

  shouldFaceUser = !shouldFaceUser;
  streamVideoFeed();
});

document
  .getElementById("capture-camera")
  .addEventListener("click", function () {

    spinner.style.display = "block"

    const video = document.querySelector("video");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext("2d").drawImage(video, 0, 0);

    canvas.toBlob((blob) => {
      let formData = new FormData();
      formData.append("image", blob, "camera-image.png");

      var xmlHttpReq = false;

      if (window.XMLHttpRequest) {
        ajax = new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        ajax = new ActiveXObject("Microsoft.XMLHTTP");
      }

      ajax.open("POST", "/upload", false);

      ajax.onreadystatechange = function () {
        console.log(ajax.responseText);
        document.getElementById('output').innerHTML = ajax.responseText;

        spinner.style.display = "none"
      };
      ajax.send(formData);
    });
  });










//Navigate via address-bar to inse in Chrome.
//
//Find and enable the Insecure origins treated as secure section.
//
//Add any addresses you want to ignore the secure origin policy for. (Include the port number if required.)
//
//Save and restart Chrome.