FROM node:18 as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY ./ .
RUN npm run build

FROM openresty/openresty:bullseye as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf