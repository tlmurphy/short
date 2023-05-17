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

const setShortUrl = (url) => {
  const shortUrl = `${server}/${url.shortUrl}`;
  document.getElementById(
    'shortened'
  ).innerHTML = `<p>Shortened URL: <a href=${shortUrl} target="_blank">${shortUrl}</a></p>`;
};

const resetShortUrl = () => {
  document.getElementById('shortened').innerHTML = '';
};

button.addEventListener('click', async () => {
  const url = input.value;
  const response = await fetch(`${server}/urls`, {
    method: 'POST',
    body: JSON.stringify({ url: url }),
    headers: { 'Content-Type': 'application/json' },
  });
  const json = await response.json();
  if (!response.ok) {
    if (json.url) {
      alert(json.message + '\nYour existing short URL will now be displayed.');
      setShortUrl(json.url);
    } else {
      alert(json.message);
      resetShortUrl();
    }
  } else {
    setShortUrl(json.url);
  }
});
