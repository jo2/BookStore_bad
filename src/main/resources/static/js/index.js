function toShoppingCart() {
  const shoppingCartRaw = sessionStorage.getItem("shoppingCart");
  let shoppingCart = [];
  if (shoppingCartRaw) {
    shoppingCart = JSON.parse(shoppingCartRaw);
    window.location = `/shopping-cart?bookIds=${shoppingCart}`
  }
}

function renderShoppingCartBadge() {
  const shoppingCart = document.getElementById('shoppingCart');
  shoppingCart.style.display = 'none';
  const shoppingCartRaw = sessionStorage.getItem("shoppingCart");
  if (shoppingCartRaw && JSON.parse(shoppingCartRaw).length > 0) {
    shoppingCart.style.display = 'inline-block';
    shoppingCart.innerText = JSON.parse(shoppingCartRaw).length;
  }
}

(function() {
  renderShoppingCartBadge();
})();
