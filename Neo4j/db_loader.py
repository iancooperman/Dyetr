import json
import uuid
from neo4j import GraphDatabase

food_items = json.load(open('usda survey data.json'))

CALORIES = 'Energy'
CARBOHYDRATES = 'Carbohydrate, by difference'
FAT = 'Total lipid (fat)'
PROTEIN = 'Protein'

CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'

db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))

# retrive specific nutrient info from a food's nutrient list
def select_nutrient(food_nutrients_list, nutrient_name):
    for nutrient in food_nutrients_list:
        if nutrient['name'] == nutrient_name:
            return nutrient['amount']
    return None

# create a food node in the Neo4j database
def create_food_item(food_id, item_name, calories, carbs, fat, protein):
    query = ('CREATE (f:food {id: $id, name: $name, calories: $calories, carbohydrates: $carbohydrates,  fat: $fat, protein: $protein})')
    with db_driver.session() as session:
        session.run(query, id = str(food_id), name = item_name, calories = calories, carbohydrates = carbs, fat = fat, protein = protein)

# iterate through eat food item in `usda survey data.json`
for item in food_items:
    food_id = uuid.uuid1()
    item_name = item['description']
    calories =  select_nutrient(item['foodNutrients'], CALORIES)
    carbs = select_nutrient(item['foodNutrients'], CARBOHYDRATES)
    fat = select_nutrient(item['foodNutrients'], FAT)
    protein = select_nutrient(item['foodNutrients'], PROTEIN)
    print("ID: {0} Item_name: {1} Calories: {2} Carbs: {3} Fat: {4} Protein: {4}".format(food_id, item_name, calories, carbs, fat, protein))
    create_food_item(food_id, item_name, calories, carbs, fat, protein)

db_driver.close()