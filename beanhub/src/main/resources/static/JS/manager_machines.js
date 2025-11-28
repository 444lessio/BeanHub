document.addEventListener("DOMContentLoaded", function() {
    const tableBody = document.getElementById("machines-body");
    const btnAdd = document.getElementById("btn-add-machine");
    const searchInput = document.getElementById("search-machine-input");

    let allMachines = [];

    async function loadMachines() {
        try {
            const response = await fetch('/data/machines.xml');
            const xmlText = await response.text();
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlText, "text/xml");

            
            const distributors = xmlDoc.querySelectorAll("distributor");

            allMachines = [];

            distributors.forEach(dist => {
                const id = dist.getAttribute("id");
                const state = dist.getAttribute("state");
                const pos = dist.querySelector("position").textContent;

                const faultsNodes = dist.querySelectorAll("faults fault");
                let faultsText = "None";
                
                if(faultsNodes.length > 0) {
                    
                    let codes = []; 
                    faultsNodes.forEach(f => codes.push(f.getAttribute("code")));
                    faultsText = codes.join(", ");
                }

                
                allMachines.push({ id, state, pos, faultsText: faultsText });
            });

            renderTable(allMachines);

        } catch (error) {
            console.error("XML Machines Load Error:", error);
        }
    }

    function renderTable(machinesList) {
        tableBody.innerHTML = "";

        if(machinesList.length === 0) {
            tableBody.innerHTML = "<tr><td colspan='5' style='text-align:center'>No machines found</td></tr>";
            return;
        }

        machinesList.forEach(m => {
            let color = "green";
            if(m.state === "maintenance") color = "#ffc107";
            if(m.state === "offline") color = "red";

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${m.id}</td>
                <td>${m.pos}</td>
                <td style="color: ${color}; font-weight:bold;">${m.state.toUpperCase()}</td>
                <td>${m.faultsText}</td>
                <td>
                    <button class="toggle" onclick="toggleStatus('${m.id}')">Toggle</button>
                    <button class="remove" onclick="removeMachine(this, '${m.id}')">Remove</button>
                </td>
            `;
            
            tableBody.appendChild(row);
        });
    }

    searchInput.addEventListener("keyup", function(e) {
        const term = e.target.value.toLowerCase();

        const filtered = allMachines.filter(m => {
            return m.id.toLowerCase().includes(term) || m.pos.toLowerCase().includes(term);
        });

        renderTable(filtered);
    });

    if(btnAdd) {
        btnAdd.addEventListener("click", function() {
            const id = document.getElementById("new-machine-id").value;
            const pos = document.getElementById("new-machine-pos").value;

            if(id && pos) {
               
                allMachines.push({ id, state: "offline", pos: pos, faultsText: "None" });
                
                alert(`Machine ${id} added to position ${pos}`)

                
                searchInput.dispatchEvent(new Event("keyup"));

               
                document.getElementById("new-machine-id").value = "";
                document.getElementById("new-machine-pos").value = ""; 
            }
        });
    }

    window.removeMachine = function(btn, id) {
        if(confirm(`Are you sure you want to remove machine ${id}?`)) {
            allMachines = allMachines.filter(m => m.id !== id);
            alert(`Distributor ${id} removed successfully.`);
            searchInput.dispatchEvent(new Event("keyup"));
        }
    };

    window.toggleStatus = function(id) {
        const machine = allMachines.find(m => m.id === id);
        if(machine) {
            if(machine.state === "active") machine.state = "maintenance";
            else if(machine.state === "maintenance") machine.state = "offline";
            else machine.state = "active";

            alert(`State of ${id} changed to ${machine.state}`);
            searchInput.dispatchEvent(new Event("keyup"));
        }
    };

    loadMachines();
});