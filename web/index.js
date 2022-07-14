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
    body: JSON.stringify({ url: url }),
    headers: { 'Content-Type': 'application/json' },
  })
    .then((res) => res.json())
    .then(
      (json) => {
        console.log(json.shortUrl);
        const shortUrl = `http://localhost:8080/${json.shortUrl}`;
        document.getElementById(
          'shortened'
        ).innerHTML = `<a href=${shortUrl} target="_blank">${shortUrl}</a>`;
      },
      (err) => console.log(err)
    );
});
