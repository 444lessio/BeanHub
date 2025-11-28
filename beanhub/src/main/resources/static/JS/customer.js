document.addEventListener("DOMContentLoaded", function() {
    //state variables
    let currentCredit = 0.0;

    //DOM elements
    const welcomeMsg = document.getElementById("welcome-msg");
    const creditSpan = document.getElementById("user-credit");

    //buttons and inputs
    const distIdInput = document.getElementById("dist-id-input");
    const topupInput = document.getElementById("topup-amount");

    const btnConnect = document.getElementById("btn-connect");
    const btnDisconnect = document.getElementById("btn-disconnect");
    const btnTopup = document.getElementById("btn-topup");

    async function loadUserProfile() {
        try {
            const response = await fetch('/data/user.json');
            const user = await response.json();

            welcomeMsg.innerText = `Hi! ${user.name}`;

            currentCredit = user.credit;
            updateCreditDisplay();

            if(user.connectedMachineId) {
                
                distIdInput.value = user.connectedMachineId;
            }
        } catch (error) {
            console.error("Profile upload error:", error);
            welcomeMsg.innerText = "Error loading data";
        }
    }

    function updateCreditDisplay() {
        
        creditSpan.innerText = `€ ${currentCredit.toFixed(2)}`;
    }

    if (btnConnect) {
        btnConnect.addEventListener("click", function() {
            
            const id = distIdInput.value.trim();
            if(id) {
                alert(`Connected to distributor ${id}`);
            } else {
                alert('Please enter a valid distributor ID');
            }
        });
    }

    if (btnDisconnect) {
        btnDisconnect.addEventListener("click", function() {
            
            const id = distIdInput.value.trim() || "???";
            alert(`Disconnected from distributor ${id}`);
            
            distIdInput.value = "";
        });
    }


    if (btnTopup) {
        btnTopup.addEventListener("click", function() {
            const amount = parseFloat(topupInput.value);

            if (amount > 0) {
                currentCredit += amount;

                updateCreditDisplay();

                alert(`Reloading € ${amount.toFixed(2)} successfully!`)

                topupInput.value = "";
            } else {
                alert('Please enter a valid top-up amount');
            }
        });
    }

    loadUserProfile();
});