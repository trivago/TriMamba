FROM node:10

WORKDIR /

COPY package*.json ./

RUN npm i

COPY . .

CMD ["npm", "start"]