import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Server started at http://localhost:" + port);

        server.createContext("/", new BirthdayCardHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class BirthdayCardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Birthday Wish Card 🎉</title>
<style>
  /* Reset & basic styling */
  * {
    margin: 0; padding: 0; box-sizing: border-box;
  }
  body {
    min-height: 100vh;
    background: radial-gradient(circle at center, #0f2027, #203a43, #2c5364);
    color: #00ffe7;
    font-family: 'Poppins', sans-serif;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    cursor: none;
    user-select: none;
    /* Neon glow */
    text-shadow:
      0 0 5px #00fff7,
      0 0 10px #00fff7,
      0 0 20px #00fff7,
      0 0 30px #00fff7;
  }
  main {
    position: relative;
    background: rgba(10,10,10,0.8);
    padding: 3rem 4rem;
    border-radius: 20px;
    box-shadow:
      0 0 20px #00ffe7,
      0 0 40px #00ffe7,
      0 0 60px #00ffe7;
    max-width: 600px;
    width: 90vw;
    text-align: center;
  }
  h1 {
    font-size: 3rem;
    margin-bottom: 1rem;
    font-weight: 900;
    letter-spacing: 0.08em;
  }
  p {
    font-size: 1.3rem;
    margin-bottom: 2rem;
    line-height: 1.6;
  }
  button {
    background: none;
    border: 2px solid #00ffe7;
    padding: 0.8rem 2rem;
    font-size: 1.2rem;
    color: #00ffe7;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 700;
    letter-spacing: 0.1em;
    user-select: none;
  }
  button:hover, button:focus {
    background: #00ffe7;
    color: #091f1d;
    box-shadow:
      0 0 10px #00ffe7,
      0 0 20px #00ffe7,
      0 0 30px #00ffe7;
    outline: none;
  }
  /* Neon fireworks canvas full screen */
  #fireworksCanvas {
    position: fixed;
    top: 0; left: 0; width: 100%; height: 100%;
    pointer-events: none;
    z-index: 1000;
  }
  /* Custom neon cursor */
  #cursor {
    position: fixed;
    top: 0; left: 0;
    width: 24px;
    height: 24px;
    margin-left: -12px;
    margin-top: -12px;
    pointer-events: none;
    border-radius: 50%;
    border: 2px solid #00ffe7;
    box-shadow:
      0 0 5px #00ffe7,
      0 0 15px #00ffe7,
      0 0 25px #00ffe7,
      0 0 40px #00ffe7;
    transition: transform 0.15s ease;
    z-index: 1001;
  }
</style>
</head>
<body>
<canvas id="fireworksCanvas" aria-hidden="true"></canvas>
<div id="cursor" aria-hidden="true"></div>
<main role="main" aria-label="Birthday Wish Card">
  <h1>Happy Birthday to You!</h1>
  <p>Wishing you a day filled with happiness, laughter, and unforgettable moments.</p>
  <button id="wishBtn" aria-label="Send Birthday Wish">Send Your Wish</button>
</main>

<script>
  // Cursor tracking
  const cursor = document.getElementById('cursor');
  window.addEventListener('mousemove', e => {
    cursor.style.left = e.clientX + 'px';
    cursor.style.top = e.clientY + 'px';
  });

  // Fireworks animation
  const canvas = document.getElementById('fireworksCanvas');
  const ctx = canvas.getContext('2d');
  let cw, ch;

  function resize() {
    cw = window.innerWidth;
    ch = window.innerHeight;
    canvas.width = cw;
    canvas.height = ch;
  }
  window.addEventListener('resize', resize);
  resize();

  // Fireworks particles
  class Particle {
    constructor(x, y, color) {
      this.x = x;
      this.y = y;
      this.color = color;
      this.radius = Math.random() * 2 + 1;
      this.alpha = 1;
      this.velocity = {
        x: (Math.random() - 0.5) * 6,
        y: (Math.random() - 0.5) * 6,
      };
      this.decay = 0.015 + Math.random() * 0.015;
    }
    update() {
      this.x += this.velocity.x;
      this.y += this.velocity.y;
      this.alpha -= this.decay;
    }
    draw(ctx) {
      ctx.save();
      ctx.globalAlpha = this.alpha;
      ctx.shadowColor = this.color;
      ctx.shadowBlur = 10;
      ctx.fillStyle = this.color;
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }
  }

  // Fireworks manager
  class Firework {
    constructor(x, y) {
      this.x = x;
      this.y = y;
      this.particles = [];
      this.colors = ['#00ffe7', '#00ffd1', '#00a8a8', '#00bfa8', '#00ffcc'];
      this.createParticles();
    }
    createParticles() {
      const numParticles = 30 + Math.floor(Math.random() * 20);
      for(let i=0; i<numParticles; i++) {
        const color = this.colors[Math.floor(Math.random() * this.colors.length)];
        this.particles.push(new Particle(this.x, this.y, color));
      }
    }
    update() {
      this.particles.forEach(p => p.update());
      this.particles = this.particles.filter(p => p.alpha > 0);
    }
    draw(ctx) {
      this.particles.forEach(p => p.draw(ctx));
    }
    isDone() {
      return this.particles.length === 0;
    }
  }

  const fireworks = [];

  function animate() {
    ctx.clearRect(0, 0, cw, ch);
    fireworks.forEach((f, i) => {
      f.update();
      f.draw(ctx);
      if(f.isDone()) fireworks.splice(i, 1);
    });
    requestAnimationFrame(animate);
  }

  animate();

  // Launch fireworks on cursor movement (debounced)
  let lastLaunch = 0;
  window.addEventListener('mousemove', e => {
    const now = Date.now();
    if(now - lastLaunch > 200) {
      fireworks.push(new Firework(e.clientX, e.clientY));
      lastLaunch = now;
    }
  });

  // Send wish button effect: flash fireworks around button
  const wishBtn = document.getElementById('wishBtn');
  wishBtn.addEventListener('click', () => {
    const rect = wishBtn.getBoundingClientRect();
    for(let i=0; i<5; i++) {
      const x = rect.left + Math.random() * rect.width;
      const y = rect.top + Math.random() * rect.height;
      fireworks.push(new Firework(x, y));
    }
    alert('Your birthday wish has been sent! Have a wonderful day!');
  });
</script>
</body>
</html>
            """;

            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes("UTF-8"));
            }
        }
    }
}

