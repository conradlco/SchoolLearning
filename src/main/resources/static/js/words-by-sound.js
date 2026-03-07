document.addEventListener('DOMContentLoaded', function() {
  const levelSelect = document.getElementById('level');
  const soundInput = document.getElementById('sound');
  const showBtn = document.getElementById('show');
  const closeBtn = document.getElementById('close');
  const results = document.getElementById('results');
  const info = document.getElementById('info');

  const MAX_ROWS = 10;
  const CAPACITY = MAX_ROWS * 3;

  function fetchLevels() {
    fetch('/api/words/levels')
      .then(r => r.json())
      .then(data => {
        levelSelect.innerHTML = '';
        data.forEach(l => {
          const opt = document.createElement('option');
          opt.value = l;
          opt.textContent = l;
          levelSelect.appendChild(opt);
        });
      });
  }

  let debounceTimer = null;
  function checkMatches() {
    const q = soundInput.value.trim();
    if (!q) {
      showBtn.disabled = true;
      info.textContent = '';
      return;
    }
    const level = levelSelect.value || 'All';
    fetch(`/api/words/search?q=${encodeURIComponent(q)}&level=${encodeURIComponent(level)}&limit=1`)
      .then(r => r.json())
      .then(data => {
        showBtn.disabled = !(data && data.length > 0);
        info.textContent = data && data.length > 0 ? '' : 'No matches';
      })
      .catch(() => {
        showBtn.disabled = true;
      });
  }

  soundInput.addEventListener('input', function() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(checkMatches, 250);
  });

  levelSelect.addEventListener('change', function() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(checkMatches, 150);
  });

  showBtn.addEventListener('click', function() {
    const q = soundInput.value.trim();
    if (!q) return;
    const level = levelSelect.value || 'All';
    fetch(`/api/words/search?q=${encodeURIComponent(q)}&level=${encodeURIComponent(level)}&limit=${CAPACITY}`)
      .then(r => r.json())
      .then(data => {
        renderResults(data);
        if (data.length >= CAPACITY) {
          info.textContent = `Showing ${data.length} of many matches`;
        } else {
          info.textContent = `Showing ${data.length} matches`;
        }
      });
  });

  closeBtn.addEventListener('click', function() {
    window.location.href = '';
  });

  function renderResults(list) {
    results.innerHTML = '';
    // create grid slots up to CAPACITY (3 columns)
    const total = CAPACITY;
    for (let i = 0; i < total; i++) {
      const slot = document.createElement('div');
      slot.className = 'cell';
      if (i < list.length) {
        const item = list[i];
        const btn = document.createElement('button');
        btn.className = 'wordBtn';
        btn.textContent = item.word;
        btn.addEventListener('click', function() {
          alert(`${item.word} (${item.detail})`);
        });
        slot.appendChild(btn);
      }
      results.appendChild(slot);
    }
  }

  // enlarge the sound input font and make bold
  soundInput.style.fontWeight = '700';
  soundInput.style.fontSize = '16px';

  fetchLevels();
});
