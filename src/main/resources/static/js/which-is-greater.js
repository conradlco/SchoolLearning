document.addEventListener('DOMContentLoaded', function() {
  const leftNumberDisplay = document.getElementById('leftNumber');
  const rightNumberDisplay = document.getElementById('rightNumber');
  const scoreDisplay = document.getElementById('score');
  const counterDisplay = document.getElementById('counter');
  const messageDisplay = document.getElementById('message');
  const greaterBtn = document.getElementById('greaterBtn');
  const equalBtn = document.getElementById('equalBtn');
  const lessBtn = document.getElementById('lessBtn');
  const settingsBtn = document.getElementById('settingsBtn');
  const closeBtn = document.getElementById('closeBtn');

  const TOTAL_QUESTIONS = 20;

  // Game state
  let currentQuestionIndex = 0;
  let score = 0;
  let currentLeft = 0;
  let currentRight = 0;

  // Configurable weighted ranges and weights (percent)
  let rangeA = 20;
  let rangeB = 50;
  let rangeC = 200;
  let weightA = 60; // percent for rangeA
  let weightB = 30; // percent for rangeB
  let weightC = 10; // percent for rangeC

  function randomNumberWithWeightedRange() {
    const p = Math.floor(Math.random() * 100); // 0..99
    let cumulative = 0;

    cumulative += weightA;
    if (p < cumulative) {
      return Math.floor(Math.random() * (rangeA + 1));
    }

    cumulative += weightB;
    if (p < cumulative) {
      return Math.floor(Math.random() * (rangeB + 1));
    }

    // fallback to C
    return Math.floor(Math.random() * (rangeC + 1));
  }

  function nextQuestion() {
    if (currentQuestionIndex >= TOTAL_QUESTIONS) {
      showEndOfGameDialog();
      return;
    }

    currentLeft = randomNumberWithWeightedRange();
    currentRight = randomNumberWithWeightedRange();

    leftNumberDisplay.textContent = currentLeft;
    rightNumberDisplay.textContent = currentRight;

    updateCounter();

    // Ensure buttons enabled
    greaterBtn.disabled = false;
    equalBtn.disabled = false;
    lessBtn.disabled = false;
    messageDisplay.textContent = '';
  }

  function updateCounter() {
    scoreDisplay.textContent = score;
    counterDisplay.textContent = (currentQuestionIndex + 1) + ' / ' + TOTAL_QUESTIONS;
  }

  function startGame() {
    currentQuestionIndex = 0;
    score = 0;
    updateCounter();
    nextQuestion();
  }

  function handleAnswer(chosen) {
    // Disable buttons to prevent double clicks
    greaterBtn.disabled = true;
    equalBtn.disabled = true;
    lessBtn.disabled = true;

    let correct;
    if (currentLeft > currentRight) correct = 0;
    else if (currentLeft === currentRight) correct = 1;
    else correct = 2;

    const isCorrect = (chosen === correct);
    if (isCorrect) score++;

    const resultMessage = (isCorrect ? '✓ Correct!' : '✗ Wrong.') + ' Score: ' + score + ' / ' + TOTAL_QUESTIONS;
    messageDisplay.textContent = resultMessage;
    messageDisplay.style.color = isCorrect ? 'green' : 'red';

    currentQuestionIndex++;

    // Move to next question after a delay
    setTimeout(() => {
      if (currentQuestionIndex >= TOTAL_QUESTIONS) {
        showEndOfGameDialog();
      } else {
        nextQuestion();
      }
    }, 1500);
  }

  greaterBtn.addEventListener('click', () => handleAnswer(0));
  equalBtn.addEventListener('click', () => handleAnswer(1));
  lessBtn.addEventListener('click', () => handleAnswer(2));

  settingsBtn.addEventListener('click', openSettingsDialog);
  closeBtn.addEventListener('click', () => {
    window.location.href = '/';
  });

  function openSettingsDialog() {
    const rangeAInput = prompt('Range A (0..N):', rangeA);
    if (rangeAInput === null) return;
    const newRangeA = parseInt(rangeAInput);
    if (isNaN(newRangeA) || newRangeA < 1) {
      alert('Invalid Range A. Must be a positive integer.');
      return;
    }

    const rangeBInput = prompt('Range B (0..N):', rangeB);
    if (rangeBInput === null) return;
    const newRangeB = parseInt(rangeBInput);
    if (isNaN(newRangeB) || newRangeB < 1) {
      alert('Invalid Range B. Must be a positive integer.');
      return;
    }

    const rangeCInput = prompt('Range C (0..N):', rangeC);
    if (rangeCInput === null) return;
    const newRangeC = parseInt(rangeCInput);
    if (isNaN(newRangeC) || newRangeC < 1) {
      alert('Invalid Range C. Must be a positive integer.');
      return;
    }

    const weightAInput = prompt('Weight A (%):', weightA);
    if (weightAInput === null) return;
    const newWeightA = parseInt(weightAInput);
    if (isNaN(newWeightA) || newWeightA < 0 || newWeightA > 100) {
      alert('Invalid Weight A. Must be between 0 and 100.');
      return;
    }

    const weightBInput = prompt('Weight B (%):', weightB);
    if (weightBInput === null) return;
    const newWeightB = parseInt(weightBInput);
    if (isNaN(newWeightB) || newWeightB < 0 || newWeightB > 100) {
      alert('Invalid Weight B. Must be between 0 and 100.');
      return;
    }

    const weightCInput = prompt('Weight C (%):', weightC);
    if (weightCInput === null) return;
    const newWeightC = parseInt(weightCInput);
    if (isNaN(newWeightC) || newWeightC < 0 || newWeightC > 100) {
      alert('Invalid Weight C. Must be between 0 and 100.');
      return;
    }

    const sum = newWeightA + newWeightB + newWeightC;
    if (sum !== 100) {
      alert('Weights must sum to 100%. Current sum: ' + sum);
      return;
    }

    // Apply new configuration
    rangeA = newRangeA;
    rangeB = newRangeB;
    rangeC = newRangeC;
    weightA = newWeightA;
    weightB = newWeightB;
    weightC = newWeightC;

    alert('Settings saved successfully!');
  }

  function showEndOfGameDialog() {
    const summary = 'You scored ' + score + ' out of ' + TOTAL_QUESTIONS + '.\n\nWhat would you like to do?';
    const choice = confirm(summary + '\n\nOK = Play Again\nCancel = Return to Exercises');

    if (choice) {
      // Play Again
      startGame();
    } else {
      // Return to exercises
      window.location.href = '/';
    }
  }

  // Start the game
  startGame();
});

