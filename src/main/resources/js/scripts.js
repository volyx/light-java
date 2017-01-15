//document.getElementsByTagName("h1");

var s = document.getElementsByTagName('h1')[0].style;
s.opacity = 1;

(function fade() {
    (s.opacity-=.1) < 0 ? s.display="none" :setTimeout(fade,40)
})();


function getJSON(url, next) {
    var request = new XMLHttpRequest();

    if("withCredentials" in request)
      {
       request.open("GET", url, true);
       request.send();
      }

    request.addEventListener("load", function () {
        if (request.status < 200 && request.status >= 400) {
            return next(new Error("We reached our target server, but it returned an error."));
        }

        next(null, JSON.parse(request.responseText));
    });

    request.addEventListener("error", function () {
        next(new Error("There was a connection error of some sort."));
    });
}

getJSON('/json', function (err, data) {
    if (err) {
      return err;
    }

    document.querySelector("#text").innerHTML = data.name;
});

