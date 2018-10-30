# proxied-suggest-requester
> Parses each line of an input file through the google suggest interface and saves return text to output file.

Use case is to monitor a large amount of suggests (for example you're an economist and you want to know how many companies have "careers" in their suggests). It's a small, fast script that's proven to work well with millions of queries per day.

Features:
- Socket Proxy Support (add your own proxies)
- uses filesystem instead of database (quicker for a lot of requests)

Example output:

<img src="https://res.cloudinary.com/dgcz3spln/image/upload/v1537868682/Unbenannzugjkt.png" width="40%" >

