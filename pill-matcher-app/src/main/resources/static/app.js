const video = document.getElementById("video");

function startVideo() {
  navigator.mediaDevices
    .getUserMedia({
      video: true,
    })
    .then(
      (stream) => (video.srcObject = stream),
      (err) => console.log(err)
    );
}

startVideo();

// camera stream video element
let on_stream_video = document.querySelector("#camera-stream");

// flip button element
let flipBtn = document.querySelector("#flip-btn");

// default user media options
let constraints = { audio: false, video: true };

// should default to false for pill photo and if has rear camera, else true
let shouldFaceUser = true;

// check whether we can use facingMode
let supports = navigator.mediaDevices.getSupportedConstraints();
if (supports["facingMode"] === true) {
  flipBtn.disabled = false;
}

let stream = null;

function capture() {
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

flipBtn.addEventListener("click", function () {
  if (stream == null) return;
  // we need to flip, stop everything
  stream.getTracks().forEach((t) => {
    t.stop();
  });
  // toggle / flip
  shouldFaceUser = !shouldFaceUser;
  capture();
});

capture();

document
  .getElementById("capture-camera")
  .addEventListener("click", function () {

    const video = document.querySelector("video");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext("2d").drawImage(video, 0, 0);

    canvas.toBlob((blob) => {
      let formData = new FormData();
      formData.append("image", blob, "file.png");

      var xmlHttpReq = false;

      if (window.XMLHttpRequest) {
        ajax = new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        ajax = new ActiveXObject("Microsoft.XMLHTTP");
      }

      ajax.open("POST", "/upload", false);

      ajax.onreadystatechange = function () {
        console.log(ajax.responseText);
      };
      ajax.send(formData);
    });
  });
