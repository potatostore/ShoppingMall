FROM node:20-alpine
WORKDIR /app
COPY shopping-mall-web/package.json shopping-mall-web/package-lock.json ./
RUN npm cache clean --force && npm install
RUN npm install
COPY shopping-mall-web/ .
EXPOSE 3000
CMD ["npm", "run", "dev"]