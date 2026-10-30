const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3030;
const MIME = {
  '.html': 'text/html',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.css': 'text/css',
  '.js': 'application/javascript'
};

const server = http.createServer((req, res) => {
  let file = req.url === '/' ? '/index.html' : req.url;
  let p = path.join(__dirname, decodeURIComponent(file.split('?')[0]));
  if (!fs.existsSync(p)) {
    res.writeHead(404);
    res.end('Not found');
    return;
  }
  let ext = path.extname(p);
  res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream' });
  fs.createReadStream(p).pipe(res);
});

server.listen(PORT, () => {
  console.log(`Video studio running at http://localhost:${PORT}`);
});
