const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8084/ws'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);

    // Subscribe to /topic/prices
    stompClient.subscribe('/topic/prices', (message) => {
        try {
            // Correctly parse the response
            const data = JSON.parse(message.body);
            
            // Get the latest prices and price history
            const stockData = data.latestPrices;
            const priceHistory = data.priceHistory;

            console.log("Historical Data:", priceHistory);

            // Loop through stockData to display prices
            stockData.forEach((stock) => {
                console.log(`Stock Name: ${stock.name}`);
                console.log(`   Quantity: ${stock.quantity}`);
                console.log(`   Current Price: ${stock.currentPrice.toFixed(2)}`);
                console.log(`   Min Price: ${stock.priceMin}`);
                console.log(`   Max Price: ${stock.priceMax}`);
                console.log('--------------------------');
            });

            // Optional: Loop through priceHistory if needed
            Object.keys(priceHistory).forEach(stockName => {
                console.log(`History for ${stockName}:`);
                console.log(priceHistory[stockName])
                console.log('--------------------------');
            });

        } catch (error) {
            console.error('Error parsing stock data:', error);
        }
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

// Set button states and toggle conversation visibility
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// Connect WebSocket
function connect() {
    stompClient.activate();
}

// Disconnect WebSocket
function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

// Send message (optional)
function sendName() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'name': $("#name").val()})
    });
}

// Show greeting in HTML table
function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

// jQuery to handle form submission and button actions
$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});
