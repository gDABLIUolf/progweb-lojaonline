document.addEventListener('DOMContentLoaded', () => {
    // Minimal Navbar scroll effect
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        if (window.scrollY > 30) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Mobile Menu Toggle
    const mobileToggle = document.querySelector('.mobile-toggle');
    const navLinks = document.querySelector('.nav-links');
    
    if (mobileToggle) {
        mobileToggle.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            
            const icon = mobileToggle.querySelector('i');
            if (icon.classList.contains('ph-list')) {
                icon.classList.remove('ph-list');
                icon.classList.add('ph-x');
            } else {
                icon.classList.remove('ph-x');
                icon.classList.add('ph-list');
            }
        });
    }

    // Gentle Fade-in Animation Observer
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.15
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target); // Run once
            }
        });
    }, observerOptions);

    const fadeElements = document.querySelectorAll('.fade-in');
    fadeElements.forEach(el => observer.observe(el));

    // UI Functionality: Modals & Sidebar
    const searchOverlay = document.getElementById('searchOverlay');
    const loginModal = document.getElementById('loginModal');
    const cartSidebar = document.getElementById('cartSidebar');
    const cartOverlayBg = document.getElementById('cartOverlayBg');

    // Open Buttons
    const openSearchBtn = document.getElementById('openSearch');
    const openLoginBtn = document.getElementById('openLogin');
    const openCartBtn = document.getElementById('openCart');

    // Close Buttons
    const closeSearchBtn = document.getElementById('closeSearch');
    const closeLoginBtn = document.getElementById('closeLogin');
    const closeCartBtn = document.getElementById('closeCart');

    // Search Logic
    if (openSearchBtn) {
        openSearchBtn.addEventListener('click', (e) => {
            e.preventDefault();
            searchOverlay.classList.add('active');
            setTimeout(() => {
                searchOverlay.querySelector('input').focus();
            }, 300);
        });
    }
    
    if (closeSearchBtn) {
        closeSearchBtn.addEventListener('click', () => {
            searchOverlay.classList.remove('active');
        });
    }

    // Auth Flow Logic (Mock Database)
    const mockRegisteredEmails = ['teste@teste.com', 'admin@vestebem.com'];

    const stepEmail = document.getElementById('stepEmail');
    const stepLogin = document.getElementById('stepLogin');
    const stepRegister = document.getElementById('stepRegister');
    const authTitle = document.getElementById('authTitle');
    const authSubtitle = document.getElementById('authSubtitle');
    const authEmailInput = document.getElementById('authEmailInput');
    const btnContinueEmail = document.getElementById('btnContinueEmail');
    
    const loginEmailDisplay = document.getElementById('loginEmailDisplay');
    const registerEmailDisplay = document.getElementById('registerEmailDisplay');
    const backBtns = document.querySelectorAll('.btn-back');

    function resetAuthModal() {
        stepEmail.style.display = 'block';
        stepLogin.style.display = 'none';
        stepRegister.style.display = 'none';
        authTitle.textContent = 'Acesse sua conta';
        authSubtitle.textContent = 'Insira seu e-mail para entrar ou cadastrar.';
        authEmailInput.value = '';
    }

    if (btnContinueEmail) {
        btnContinueEmail.addEventListener('click', () => {
            const email = authEmailInput.value.trim();
            if (!email || !email.includes('@')) {
                alert('Por favor, insira um e-mail válido.');
                return;
            }

            stepEmail.style.display = 'none';

            if (mockRegisteredEmails.includes(email.toLowerCase())) {
                // Email exists -> Login
                stepLogin.style.display = 'block';
                loginEmailDisplay.textContent = email;
                authTitle.textContent = 'Bem-vindo de volta';
                authSubtitle.textContent = 'Insira sua senha para acessar.';
            } else {
                // New Email -> Register
                stepRegister.style.display = 'block';
                registerEmailDisplay.textContent = email;
                authTitle.textContent = 'Crie sua conta';
                authSubtitle.textContent = 'Preencha seus dados para finalizar.';
            }
        });
    }

    backBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            stepEmail.style.display = 'block';
            stepLogin.style.display = 'none';
            stepRegister.style.display = 'none';
            authTitle.textContent = 'Acesse sua conta';
            authSubtitle.textContent = 'Insira seu e-mail para entrar ou cadastrar.';
        });
    });

    if (openLoginBtn) {
        openLoginBtn.addEventListener('click', (e) => {
            e.preventDefault();
            resetAuthModal();
            loginModal.classList.add('active');
        });
    }

    if (closeLoginBtn) {
        closeLoginBtn.addEventListener('click', () => {
            loginModal.classList.remove('active');
        });
    }

    // Close login if clicked outside
    loginModal.addEventListener('click', (e) => {
        if (e.target === loginModal) {
            loginModal.classList.remove('active');
        }
    });

    // Cart Logic
    if (openCartBtn) {
        openCartBtn.addEventListener('click', (e) => {
            e.preventDefault();
            cartSidebar.classList.add('active');
            cartOverlayBg.classList.add('active');
        });
    }

    if (closeCartBtn) {
        closeCartBtn.addEventListener('click', () => {
            cartSidebar.classList.remove('active');
            cartOverlayBg.classList.remove('active');
        });
    }

    cartOverlayBg.addEventListener('click', () => {
        cartSidebar.classList.remove('active');
        cartOverlayBg.classList.remove('active');
    });

    // Add to cart buttons also open the sidebar (Simulation)
    const addToCartBtns = document.querySelectorAll('.add-to-cart-btn');
    addToCartBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            // Just open the cart to simulate adding an item
            cartSidebar.classList.add('active');
            cartOverlayBg.classList.add('active');
        });
    });
});
