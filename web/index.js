const input = document.querySelector('input');
const button = document.querySelector('button');

button.addEventListener('click', () => {
  const url = input.value;
  document.getElementById('shortened').textContent = url;
});
