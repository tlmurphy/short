const input = document.querySelector('input');
const button = document.querySelector('button');

// Window event listener for handling "Enter" key, which will click the button
window.addEventListener('keypress', (event) => {
  if (event.key === 'Enter') {
    event.preventDefault();
    button.click();
  }
});

button.addEventListener('click', () => {
  const url = input.value;
  document.getElementById('shortened').textContent = url;
});
