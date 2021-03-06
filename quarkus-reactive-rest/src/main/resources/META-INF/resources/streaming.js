if (window.EventSource) {
    const eventSource = new EventSource("/stream/Quarkus");
    eventSource.onmessage = function (event) {
        var container = document.getElementById("container");
        var paragraph = document.createElement("p");
        paragraph.innerHTML = event.data;
        container.appendChild(paragraph);
    };
} else {
    window.alert("EventSource not available on this browser.")
}