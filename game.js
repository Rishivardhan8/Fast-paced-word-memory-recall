const words = [
  'Accept','Except','Affect','Effect','Allusion','Illusion',
  'Breath','Breathe','Capital','Capitol','Complement','Compliment',
  'Desert','Dessert','Principal','Principle','Stationary','Stationery',
  'Ensure','Insure','Implicit','Explicit','Loose','Lose',
  'Patience','Patients','Peace','Piece','Resign','Re-sign',
  'Weather','Whether','Advice','Advise','Lead','Led','Role','Roll',
  'Through','Threw','Access','Excess','Addition','Edition','Dual','Duel',
  'Hoard','Horde','Idle','Idol','Pedal','Peddle','Sole','Soul','Waive','Wave'
];

document.getElementById("modeSelect").onchange = () => {
  let mode = document.getElementById("modeSelect").value;
  document.getElementById("livesSelect").style.display = mode === "lives" ? "block" : "none";
  document.getElementById("timerSelect").style.display = mode === "timer" ? "block" : "none";
};

class Game {
  constructor() {
    this.score = 0;
    this.lives = 0;
    this.time = 0;
    this.timer = null;
    this.seen = new Set();
    this.word = "";
    this.busy = false;
    this.mode = "lives";
  }

  saveScore() {
    let scores = JSON.parse(localStorage.getItem("vm_scores")) || [];
    scores.push(this.score);
    scores.sort((a, b) => b - a);
    scores = scores.slice(0, 10);
    localStorage.setItem("vm_scores", JSON.stringify(scores));
  }

  start() {
    this.score = 0;
    this.seen.clear();
    this.busy = false;
    clearInterval(this.timer);

    let mode = document.getElementById("modeSelect").value;

    if (mode === "lives") {
      this.mode = "lives";
      this.lives = parseInt(document.getElementById("livesSelect").value);
    } else {
      this.mode = "timer";
      this.time = parseInt(document.getElementById("timerSelect").value);
      this.tick();
    }

    document.getElementById("menu").classList.remove("active");
    document.getElementById("game").classList.add("active");

    this.update();
    this.next();
  }

  tick() {
    this.timer = setInterval(() => {
      if (this.busy) return;
      this.time--;
      this.update();
      if (this.time <= 0) this.end();
    }, 1000);
  }

  next() {
    let repeatChance = Math.min(0.35 + this.score * 0.015, 0.85);

    if (this.seen.size > 2 && Math.random() < repeatChance) {
      let arr = [...this.seen];
      this.word = arr[Math.floor(Math.random() * arr.length)];
    } else {
      let w = words[Math.floor(Math.random() * words.length)];
      while (this.seen.has(w) && this.seen.size < words.length) {
        w = words[Math.floor(Math.random() * words.length)];
      }
      this.word = w;
    }

    let el = document.getElementById("word");
    el.textContent = this.word;
    el.classList.remove("shake");
    void el.offsetWidth;
  }

  choose(seen) {
    if (this.busy) return;

    let wasSeen = this.seen.has(this.word);

    if ((seen && wasSeen) || (!seen && !wasSeen)) {
      this.score++;
      this.seen.add(this.word);

      if (this.mode === "timer") this.time += 1;

      this.next();
    } else {
      let el = document.getElementById("word");
      el.classList.add("shake");

      if (this.mode === "timer") {
        this.time -= 5;
        if (this.time <= 0) return this.end();
      }

      if (this.mode === "lives") {
        this.lives--;
        if (this.lives <= 0) return this.end();
      }

      this.busy = true;
      setTimeout(() => {
        this.busy = false;
        this.next();
      }, 450);
    }

    this.update();
  }

  update() {
    document.getElementById("score").textContent = this.score;

    if (this.mode === "lives") {
      document.getElementById("modeUI").textContent = "Lives: " + this.lives;
    } else {
      document.getElementById("modeUI").textContent = this.time + "s";
    }
  }

  end() {
    clearInterval(this.timer);
    this.saveScore();

    document.getElementById("game").classList.remove("active");
    document.getElementById("over").classList.add("active");
    document.getElementById("finalScore").textContent = this.score;
  }
}

const game = new Game();

function showScores() {
  document.getElementById("menu").classList.remove("active");
  document.getElementById("scores").classList.add("active");

  let scores = JSON.parse(localStorage.getItem("vm_scores")) || [];
  let list = document.getElementById("scoreList");
  list.innerHTML = "";

  scores.forEach((s, i) => {
    let li = document.createElement("li");
    li.textContent = (i + 1) + ". " + s;
    list.appendChild(li);
  });
}

function backToMenu() {
  document.getElementById("scores").classList.remove("active");
  document.getElementById("menu").classList.add("active");
}
