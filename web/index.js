const input = document.querySelector('input');
const button = document.querySelector('button');
const server = 'http://localhost:8081';

// Window event listener for handling "Enter" key, which will click the button
window.addEventListener('keypress', (event) => {
  if (event.key === 'Enter') {
    event.preventDefault();
    button.click();
  }
});

button.addEventListener('click', async () => {
  const url = input.value;
  const response = await fetch(`${server}/urls`, {
    method: 'POST',
    body: JSON.stringify({ url: url }),
    headers: { 'Content-Type': 'application/json' },
  });
  const json = await response.json();
  if (!response.ok) {
    alert(json.message);
  } else {
    const shortUrl = `${server}/${json.url.shortUrl}`;
    document.getElementById(
      'shortened'
    ).innerHTML = `<a href=${shortUrl} target="_blank">${shortUrl}</a>`;
  }
});
