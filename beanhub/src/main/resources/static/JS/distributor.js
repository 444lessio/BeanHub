document.addEventListener("DOMContentLoaded", function() {

    //state variables
    let currentUser = null;
    let currentCredit = 0.0;

    //DOM elements
    const nameElement = document.getElementById("user-name");
    const creditElement = document.getElementById("user-credit");
    const sugarSlider = document.getElementById("sugar-slider");
    const sugarValueDisplay = document.getElementById("sugar-value");

    async function fetchUserData() {
        try {
            const response = await fetch('/data/user.json');
            if (!response.ok) {
                throw new Error('Data loading error');
            }

            const userData = await response.json();

            currentUser = userData;

           
            if (currentCredit === 0.0) {
                currentCredit = userData.credit;
            }

            updateDisplay();

        } catch (error) {
            console.error("Polling error", error);
            nameElement.innerText = "Disconnected / Mistake";
            creditElement.innerText = "---";
        }
    }

    function updateDisplay() {
        if (currentUser) {
            nameElement.innerText = currentUser.name;
            creditElement.innerText = `€ ${currentCredit.toFixed(2)}`;
        }
    }

   
    if(sugarSlider) {
        sugarSlider.addEventListener("input", function(){
            const val = parseInt(this.value);
            let text ="";

            if (val === 0) text = "No sugar";
            else if (val <= 2) text = "Low";
            else if (val <= 4) text = "Medium";
            else text = "High";

            sugarValueDisplay.innerText = text;
        });
    }

   
    const drinkButtons = document.querySelectorAll(".drink-btn");

 
    drinkButtons.forEach(btn => {
        btn.addEventListener("click", function() {
          
            const price = parseFloat(this.getAttribute("data-price"));
            const drinkName = this.innerText;

            if (currentCredit >= price) {
                currentCredit -= price;

                updateDisplay();

                alert(`Disbursement ${drinkName} in progress...\nResidual credit: € ${currentCredit.toFixed(2)}`);
            } else {
                
                alert("Insufficient credit! Please recharge.");
            }
        });
    });

    
    fetchUserData();

   
    setInterval(fetchUserData, 5000);

});