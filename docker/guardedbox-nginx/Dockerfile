FROM nginx:1.15.7-alpine
MAINTAINER s3curitybug@gmail.com
COPY nginx/guardedbox.conf /etc/nginx/conf.d/guardedbox.conf
RUN rm /etc/nginx/conf.d/default.conf \
	&& chmod 444 /etc/nginx/conf.d/guardedbox.conf
