document.addEventListener("DOMContentLoaded", function() {
    const tableBody = document.getElementById("attendants-body");
    const btnAdd = document.getElementById("btn-add-attendant");
    const inputName = document.getElementById("new-name");
    const inputEmail = document.getElementById("new-email");


    async function loadAttendants() {
        try {
            const response = await fetch('/data/attendants.xml');
            const xmlText = await response.text();
            const parser = new DOMParser();
            
            
            const xmlDoc = parser.parseFromString(xmlText, "text/xml");

            
            const attendants = xmlDoc.querySelectorAll("attendant");

            tableBody.innerHTML = "";

            attendants.forEach(att => {
                const id = att.getAttribute("id");
                const name = att.querySelector("name").textContent;
                const email = att.querySelector("email").textContent;
                
                
                addResultRow(id, name, email);
            });
        } catch (error) {
            console.error("XML Error:", error);
        }
    }

    function addResultRow(id, name, email) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${id}</td>
            <td>${name}</td>
            <td>${email}</td>
            <td><button class="remove remove-btn" onclick="removeRow(this, '${id}')">Remove</button></td>
        `;
        tableBody.appendChild(row);
    }

    if (btnAdd) {
        btnAdd.addEventListener("click", function() {
            const name = inputName.value;
            const email = inputEmail.value;

            if (name && email) {
                const fakeId = 'A-' + Math.floor(Math.random() * 1000);

                alert(`${name} clerk added successfully! (ID: ${fakeId})`);

                addResultRow(fakeId, name, email);

                inputName.value = "";
                inputEmail.value = "";
            } else {
                alert("Please fill in all fields.");
            }
        });
    }

    
    window.removeRow = function(button, id) {
        if (confirm(`Are you sure you want to remove attendant with ID: ${id}?`)) {
            
            
            button.closest("tr").remove();
            
            alert(`Attendant with ID: ${id} removed successfully!`);
        }
    };

    loadAttendants();
});