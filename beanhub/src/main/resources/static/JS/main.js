
function toggleLangDropdown() {
    document.getElementById("lang-dropdown").classList.toggle("show");
}

window.onclick = function(event) {

    
    if (!event.target.closest('.dropdown-toggle')) {

        var dropdowns = document.getElementsByClassName("dropdown-content");

        
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}