import uuid
from neo4j import GraphDatabase
from flask import Flask, request

app = Flask(__name__)


CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'

db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))


@app.route('/api/v1/ingest', methods=["POST"])
def add_food():
    id = uuid.uuid1()
    food_item = request.get_json()
    name = food_item['name']
    calories = food_item['calories']
    carbohydrates = food_item['carbohydrates']
    fat = food_item['fat']
    protein = food_item['protein']
    new_food_item = ('CREATE (f:food {id: $id, name: $name, calories: $calories, carbohydrates: $carbohydrates,  fat: $fat, protein: $protein})')
    with db_driver.session() as session:
        session.run(new_food_item, id = str(id), name = name, calories = calories, carbohydrates = carbohydrates, fat = fat, protein = protein)
    return '', 201

    


app.run()
db_driver.close()
