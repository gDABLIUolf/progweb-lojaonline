<template>
  <header class="hero">
    <div class="container">
      <div class="hero-grid">
        <div class="hero-text fade-in-up">
          <h1>O Essencial, Elevado.</h1>

          <p>
            Descubra a nova coleção de peças atemporais feitas com algodão pima
            e tecidos sustentáveis. Um toque suave e um caimento impecável.
          </p>

          <button class="btn-premium mt-3">Comprar Coleção</button>
        </div>

        <div class="hero-img-wrap fade-in-up" style="transition-delay: 0.2s">
          <img
            :src="heroImage"
            alt="VesteBem Minimal Fashion"
            class="hero-img"
          />
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import heroImage from "../../assets/img/hero_minimal_fashion_1780402716026.png";
</script>

<style scoped>
.hero {
  min-height: 80vh;
  display: flex;
  align-items: center;
  padding: 4rem 0;
}

.hero-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-text {
  animation: fadeInUp 1s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

.hero-text h1 {
  font-size: clamp(2.5rem, 5vw, 4.5rem);
  font-weight: 600;
  line-height: 1.1;
  letter-spacing: -0.02em;
  margin-bottom: 1.5rem;
}

.hero-text p {
  color: var(--text-secondary);
  font-size: 1.15rem;
  font-weight: 300;
  max-width: 90%;
  margin-bottom: 2rem;
}

.hero-img-wrap {
  width: 100%;
  aspect-ratio: 4 / 5;

  overflow: hidden;
  border-radius: var(--radius-lg);

  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);

  animation: fadeInUp 1s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;

  transition: transform 0.6s ease;
}

.hero-img:hover {
  transform: scale(1.03);
}

.fade-in-up {
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Tablet */

@media (max-width: 992px) {
  .hero {
    min-height: auto;
    padding: 3rem 0;
  }

  .hero-grid {
    grid-template-columns: 1fr;
    gap: 3rem;
    text-align: center;
  }

  .hero-text p {
    max-width: 100%;
    margin-left: auto;
    margin-right: auto;
  }

  .hero-img-wrap {
    max-width: 500px;
    margin: 0 auto;
  }
}

/* Mobile */

@media (max-width: 576px) {
  .hero {
    padding: 2rem 0;
  }

  .hero-text h1 {
    font-size: 2.4rem;
  }

  .hero-text p {
    font-size: 1rem;
  }

  .hero-grid {
    gap: 2rem;
  }
}
</style>
