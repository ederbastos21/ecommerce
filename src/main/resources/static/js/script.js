// Category scrolling functionality
function scrollCategories(direction) {
    const container = document.getElementById('category-filter-list');
    const scrollAmount = 200;

    if (direction === 'left') {
        container.scrollLeft -= scrollAmount;
    } else {
        container.scrollLeft += scrollAmount;
    }
}

// Category selection
document.querySelectorAll('.category-item').forEach(item => {
    item.addEventListener('click', function(e) {
        e.preventDefault();

        // Remove active class from all categories
        document.querySelectorAll('.category-item').forEach(cat => {
            cat.classList.remove('active');
        });

        // Add active class to clicked category
        this.classList.add('active');

        // Here you would typically filter products by category
        const category = this.getAttribute('data-category');
        console.log('Selected category:', category);
    });
});

// Add to cart functionality (placeholder)
function addToCart(productId) {
    console.log('Adding product to cart:', productId);

    // Update cart count
    const cartCount = document.getElementById('cart-items-count');
    const currentCount = parseInt(cartCount.textContent);
    cartCount.textContent = currentCount + 1;
}

// Search functionality (placeholder)
document.getElementById('product-search-btn').addEventListener('click', function() {
    const searchTerm = document.getElementById('product-search-input').value;
    console.log('Searching for:', searchTerm);
});

// Enter key search
document.getElementById('product-search-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        const searchTerm = this.value;
        console.log('Searching for:', searchTerm);
    }
});