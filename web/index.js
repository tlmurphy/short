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
  fetch('http://localhost:8080/urls', {
    method: 'POST',
    body: JSON.stringify({ shortUrl: 'something', originalUrl: url }),
    headers: { 'Content-Type': 'application/json' },
  })
    .then((res) => res.json())
    .then(
      (json) => {
        console.log(json);
        document.getElementById('shortened').textContent = url;
      },
      (err) => console.log(err)
    );
});
