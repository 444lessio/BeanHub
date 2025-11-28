document.addEventListener("DOMContentLoaded", function() {

    //DOM elements
    const bntLoad = document.getElementById("btn-load");
    const inputId = document.getElementById("dist-id-input");
    const resultsCard = document.getElementById("results-card");

    //output elements
    const resultHeader = document.getElementById("result-header"); 
    const valCoffee = document.getElementById("val-coffee");
    const valCups = document.getElementById("val-cups");
    const valSugar = document.getElementById("val-sugar");
    const valFaults = document.getElementById("val-faults");

    if (bntLoad) {
        bntLoad.addEventListener("click", async function() {
            const searchId = inputId.value.trim();

            if(searchId) {
                loadMachineData(searchId);
            } else {
                alert('Please enter a valid attendant ID');
                resultsCard.style.display = "none";
            }
        });
    }

    async function loadMachineData(idToFind) {
        try {
            const response = await fetch('/data/machines.xml');
            const xmlText = await response.text();

            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlText, "text/xml");

            const machine = xmlDoc.querySelector(`distributor[id='${idToFind}']`);

            if(machine) {
                resultsCard.style.display = "block";

                resultHeader.innerText = `Status Distributor ${idToFind}`;

                valCoffee.innerText = machine.querySelector("supply coffee").textContent;
                valCups.innerText = machine.querySelector("supply cups").textContent;
                valSugar.innerText = machine.querySelector("supply sugar").textContent;

                const faultsList = machine.querySelectorAll("faults fault");

                if (faultsList.length > 0) {
                    let faultsText = [];
                    faultsList.forEach(f => {
                        faultsText.push(`${f.textContent} (${f.getAttribute("code")})`);
                    });
                    
                    valFaults.innerText = faultsText.join(", ");
                    
                    valFaults.style.color = "#dc3545"; 
                } else {
                    
                    valFaults.innerText = "None / Active";
                    
                    valFaults.style.color = "#28a745"; 
                }

            } else {
                resultsCard.style.display = "none";
                alert(`Error: No distributor found with ID "${idToFind}"`);
            }

        } catch (error) {
            console.error('XML reading error:', error);
            alert("Technical error loading XML file");
        }
    }
});