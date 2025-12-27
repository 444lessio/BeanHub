document.addEventListener('DOMContentLoaded', () => {
    
    // 1. Identifichiamo CHI è questo distributore leggendolo dall'HTML
    const distributorId = document.body.getAttribute('data-distributor-id');
    const displayIdLabel = document.getElementById('disp-id-display');
    if(displayIdLabel) displayIdLabel.innerText = distributorId;

    // Elementi UI
    const body = document.body;
    const userNameDisplay = document.getElementById('user-name');
    const userCreditDisplay = document.getElementById('user-credit');
    
    // Variabili acquisto
    const purchaseBtn = document.getElementById('purchase');
    const sugarSlider = document.getElementById('sugar-slider');
    const drinkButtons = document.querySelectorAll('.drink-btn');
    
    let selectedDrink = null;
    let selectedPrice = 0.0;
    
    // --- FUNZIONE POLLING (Cuore dello Scenario B) ---
    // Chiede al server ogni 2 secondi se c'è un utente connesso
    async function checkConnectionStatus() {
        try {
            const response = await fetch(`/api/distributor/${distributorId}/status`);
            if (response.ok) {
                const data = await response.json();
                
                if (data.connected) {
                    // C'è un utente! Mostriamo l'interfaccia
                    body.classList.remove('waiting-mode');
                    body.classList.add('active-mode');
                    
                    // Aggiorniamo i dati
                    userNameDisplay.textContent = data.username;
                    userCreditDisplay.textContent = `€ ${data.credit.toFixed(2)}`;
                } else {
                    // Nessuno connesso, torniamo in attesa
                    body.classList.remove('active-mode');
                    body.classList.add('waiting-mode');
                    resetSelection();
                }
            }
        } catch (error) {
            console.error("Errore polling:", error);
        }
    }

    // Avvia il controllo ogni 2 secondi
    setInterval(checkConnectionStatus, 2000);
    checkConnectionStatus(); // Primo controllo immediato


    // --- GESTIONE ZUCCHERO E SELEZIONE ---
    const sugarLabels = ["Nessuno", "Poco", "Medio", "Molto", "Troppo", "Diabete"];
    
    if(sugarSlider) {
        sugarSlider.addEventListener('input', (e) => {
            document.getElementById('sugar-value').innerText = sugarLabels[e.target.value];
        });
    }

    drinkButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            drinkButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            selectedPrice = parseFloat(this.getAttribute('data-price'));
            selectedDrink = this.innerText;

            purchaseBtn.style.display = 'block';
            purchaseBtn.innerHTML = `Compra <b>${selectedDrink}</b> per € ${selectedPrice.toFixed(2)}`;
        });
    });

    // --- ACQUISTO (Senza CSRF Header) ---
    if(purchaseBtn) {
        purchaseBtn.addEventListener('click', async () => {
            if (!selectedDrink) return;

            try {
                // Chiamata all'endpoint specifico con ID
                const response = await fetch(`/api/distributor/${distributorId}/buy`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        drinkName: selectedDrink,
                        sugar: parseInt(sugarSlider.value),
                        price: selectedPrice
                    })
                });

                if (response.ok) {
                    const data = await response.json();
                    alert(`Erogato: ${data.message}`);
                    userCreditDisplay.textContent = `€ ${data.newCredit.toFixed(2)}`;
                    resetSelection();
                } else {
                    const err = await response.text();
                    alert("Errore: " + err);
                }
            } catch (e) {
                console.error(e);
                alert("Errore di comunicazione");
            }
        });
    }

    function resetSelection() {
        drinkButtons.forEach(b => b.classList.remove('active'));
        selectedDrink = null;
        if(purchaseBtn) purchaseBtn.style.display = 'none';
        if(sugarSlider) sugarSlider.value = 2;
    }
});