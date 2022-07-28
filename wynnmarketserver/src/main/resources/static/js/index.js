let isAllPct = false;
let isStatPct = false;

function statRedirect() {
    document.getElementById("searchBarButton").classList.add("is-loading")


    var regex = /[^A-Za-z0-9]/g;
    try {
        var selectedCategory = document.getElementById("categoryDropdown").querySelector(".is-active").innerText.replaceAll(regex, "").toLowerCase();
    } catch (e) {
        var selectedCategory = "any";
    }
    try {
        var selectedType = document.getElementById("typeDropdown").querySelector(".is-active").innerText.replaceAll(regex, "").toLowerCase();
    }catch (e) {
        var selectedType = "any";
    }

    if(selectedCategory == "any"){
        selectedCategory = "null"
    }
    if(selectedType == "any"){
        selectedType = "null"
    }

    var name = document.getElementById("nameInput").value;
    var stat = document.getElementById("statInput").value;

    if(name == ""){
        name = "null"
    }if(stat == ""){
        stat = "null"
    }
    encodeURIComponent(stat)

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", '/items/'+ name + "/" + stat + "/" + selectedCategory + "/" + selectedType + "/" + isAllPct + "/" + isStatPct, true);
    xhttp.onload = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            document.getElementById("searchBarButton").classList.remove("is-loading")
            window.location.href = "/dataGridView.xhtml";

        } else {
            document.getElementById("searchBarButton").classList.remove("is-loading")
            document.getElementById("jsonDisplay").innerText = "Error"
        }
    }
    xhttp.send();
}

function toggleDropDown(el){
    el.classList.toggle("is-active");
}
function toggleSelected(el) {
    var parent = el.parentElement;
    var children = parent.children;
    for (var i = 0; i < children.length; i++) {
        children[i].classList.remove("is-active");
    }
    el.classList.toggle("is-active");
    var parentParent = parent.parentElement.parentElement.children[0].children[0].children[0];
    parentParent.innerHTML = el.innerHTML
    if(el.innerText == "Weapon" || el.innerText == "Armour" || el.innerText == "Accessory" || (el.innerText == "Any" && el.parentElement.parentElement.id == "categoryDropdown")){
        updateTypeDropdown(el)
    }
}

function updateTypeDropdown(el){
    var validForWeapons = ["Dagger", "Wand", "Bow", "Spear", "Relik"]
    var validForArmour = ["Helmet", "Chestplate", "Leggings", "Boots"]
    var validForAccessories = ["Ring", "Necklace", "Bracelet"]
    var itemsToUse = []
    if(el.innerText == "Weapon"){
        itemsToUse = validForWeapons
    }else if(el.innerText == "Armour"){
        itemsToUse = validForArmour
    }else if(el.innerText == "Accessory"){
        itemsToUse = validForAccessories
    }else{
        itemsToUse.push("Any")
        for(var i = 0; i < validForWeapons.length; i++){
            itemsToUse.push(validForWeapons[i])
        }
        for(var i = 0; i < validForArmour.length; i++){
            itemsToUse.push(validForArmour[i])
        }
        for(var i = 0; i < validForAccessories.length; i++){
            itemsToUse.push(validForAccessories[i])
        }
    }
    document.getElementById("typeDropdown").parentElement.children[0].children[0].children[0].innerHTML = "Type";

    var elementsToChange = []

    for(var i = 0; i < itemsToUse.length; i++){
        var emptyDropDown = document.createElement("a");
        emptyDropDown.classList.add("dropdown-item");
        emptyDropDown.setAttribute("onclick","toggleSelected(this)");
        emptyDropDown.innerHTML = itemsToUse[i];
        elementsToChange.push(emptyDropDown);
    }
    var dropDown = document.getElementById("typeDropdown").children[0];
    while(dropDown.firstChild){
        dropDown.removeChild(dropDown.firstChild);
    }
    for(var i = 0; i < elementsToChange.length; i++){
        dropDown.appendChild(elementsToChange[i]);
    }

}
function togglePercentage(el){
    if(el.id == "allPct") {
        if (isAllPct) {
            isAllPct = false;
        } else {
            isAllPct = true;
        }
    }if(el.id == "statPct") {
        if (isStatPct) {
            isStatPct = false;
        } else {
            isStatPct = true;
        }
    }
}
