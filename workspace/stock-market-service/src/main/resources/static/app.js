const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8084/ws'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);


    stompClient.subscribe('/topic/intraday', (message) => {
        try {
            const data = JSON.parse(message.body);
            
            const stockData = data.latestPrices;
            const priceHistory = data.priceHistory;
			
			console.log("Latest Prices: ",stockData)
            console.log("Historical Data:", priceHistory);

            stockData.forEach((stock) => {
                console.log(`Stock Name: ${stock.name}`);
                console.log(`   Quantity: ${stock.quantity}`);
                console.log(`   Current Price: ${stock.currentPrice.toFixed(2)}`);
                console.log(`   Min Price: ${stock.priceMin}`);
                console.log(`   Max Price: ${stock.priceMax}`);
                console.log('--------------------------');
            });

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


function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}


function sendName() {
    stompClient.publish({
        destination: "/app/hello",
        body: JSON.stringify({'name': $("#name").val()})
    });
}


function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}


$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});
