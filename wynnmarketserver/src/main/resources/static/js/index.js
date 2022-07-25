function statRedirect() {
    document.getElementById("searchBarButton").classList.add("is-loading")


    var regex = /[^A-Za-z0-9]/g;
    var selectedCategory = document.getElementById("categoryDropdown").querySelector(".is-active").innerText.replaceAll(regex,"").toLowerCase();
    var selectedType = document.getElementById("typeDropdown").querySelector(".is-active").innerText.replaceAll(regex,"").toLowerCase();

    var stat = document.getElementById("searchBarInput").value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", '/items/' + stat + "/" + selectedCategory + "/" + selectedType, true);
    xhttp.onload = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            document.getElementById("searchBarButton").classList.remove("is-loading")
            var json = JSON.stringify(JSON.parse(xhttp.responseText), null, 2).replaceAll(/\\/g, "");
            document.getElementById("jsonDisplay").innerHTML = json;

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
    if(el.innerText == "Weapon" || el.innerText == "Armour" || el.innerText == "Accessory" || el.innerText == "Any"){
        updateTypeDropdown(el)
    }
}

function updateTypeDropdown(el){
    var validForWeapons = ["Dagger", "Wand", "Bow", "Spear", "Relik"]
    var validForArmour = ["Helmet", "Chestplate", "Leggings", "Boots"]
    var validForAccessories = ["Ring", "Necklace", "Amulet"]
    var itemsToUse = []
    if(el.innerText == "Weapon"){
        itemsToUse = validForWeapons
    }else if(el.innerText == "Armour"){
        itemsToUse = validForArmour
    }else if(el.innerText == "Accessory"){
        itemsToUse = validForAccessories
    }else{
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
