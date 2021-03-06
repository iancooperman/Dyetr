from flask import Flask, request
from neo4j import GraphDatabase
from datetime import datetime
from db import db

# enum
CONNECTION_STRING = 'bolt://localhost:7687'
USER_NAME = 'neo4j'
PASSWORD = 'password'


app = Flask(__name__)
db_driver = GraphDatabase.driver(CONNECTION_STRING, auth=(USER_NAME, PASSWORD))
_db_driver = db(CONNECTION_STRING, (USER_NAME, PASSWORD))

@app.route('/api/v1/recommend', methods=['GET'])
def recommend():
    try:
        user_id = request.args.get('user_id')
        meal = request.args.get('meal')
        assert (meal in ["breakfast", "lunch", "dinner"])
    except KeyError as e:
        return ({"status": f"{str(e)}"}, 500)
    except AssertionError as e:
        return ({"status": "error", "message": '"meal" must be "breakfast", "lunch", or "dinner"'}, 500)

    response = _db_driver.recommend_foods(user_id, meal)
    
    return (response, 200)

@app.route('/api/v1/food', methods = ['GET'])
def get_food():
    food_id = request.args.get('id')
    resp = _db_driver.find_food_item_by_id(food_id)
    if resp:
        return (resp, 200)
    else:
        return '', 404

@app.route('/api/v1/food', methods=['POST'])
def post_food():
    item = request.get_json()
    _db_driver.create_food_tem(item['id'], item['name'], item['calories'], item['carbohydrates'], item['fat'], item['protein'])
    return '', 201


#URL param id
@app.route('/api/v1/user', methods=['GET'])
def get_user():
    user_id = request.args.get('id')
    resp = _db_driver.get_user_by_id(user_id)
    if resp:
        return (resp, 200)
    else:
        return ('', 404)

"""
Expects JSON following this format
{
name: <str>,
age: <int>,
weight: <int>,
calorie_goal: <int>
}
"""
@app.route('/api/v1/user/register', methods=['POST'])
def create_user():
    new_user = request.get_json()
    _db_driver.create_user(new_user)
    return ('', 201)

# URL params should be: /food_eaten?user_id=......&food_id=......&meal_type=......
@app.route('/api/v1/food_eaten', methods=["GET", "POST"])
def food_eaten():
    if request.method == "GET":
        if request.args:
            
            args = request.args

            if "user_id" in args:
                
                user_id = args["user_id"]

                all_food_eaten = _db_driver.find_food_eaten_by_user(user_id)
                
                if all_food_eaten:
                    return (all_food_eaten, 200)
                else:
                    return ('', 404)
    elif request.method == "POST":
        if request.args:
            
            args = request.args

            if "user_id" in args:
                if "food_id" in args:
                    if "meal_type" in args:

                        user_id = args["user_id"]
                        food_id = args["food_id"]
                        meal_type = args["meal_type"]

                        eat_time = str(datetime.now())
                        _db_driver.create_ate_relationship(user_id, food_id, eat_time, meal_type)
                        
                        return ('', 201)
                    
# URL params should be: /food_liked?user_id=......&food_id=......
@app.route('/api/v1/food_liked', methods=["GET", "POST", "DELETE"])
def food_liked():
    if request.method == "GET":
        if request.args:
            
            args = request.args

            if "user_id" in args:
                
                user_id = args["user_id"]

                all_food_liked = _db_driver.find_food_liked_by_user(user_id)
                
                if all_food_liked:
                    return (all_food_liked, 200)
                else:
                    return ('', 404)
    elif request.method == "POST":
        if request.args:
            
            args = request.args

            if "user_id" in args:
                if "food_id" in args:
                    
                    user_id = args["user_id"]
                    food_id = args["food_id"]

                    _db_driver.create_likes_relationship(user_id, food_id)
                    
                    return ('', 201)
    elif request.method == "DELETE":
        if request.args:
            
            args = request.args

            if "user_id" in args:
                if "food_id" in args:

                    user_id = args["user_id"]
                    food_id = args["food_id"]

                    _db_driver.delete_likes_relationship(user_id, food_id)
                    
                    return ('', 201)

@app.route('/api/v1/search', methods=['GET'])
def search():
    search_query = request.args.get('q')
    
    try:
        assert search_query != None
    except AssertionError as e:
        return {"status": "You must provide a search query under parameter 'q'"}, 500

    resp = _db_driver.find_food_items_by_text_search(search_query)
    if resp:
        return resp, 200
    else:
        return "", 500


db_driver.close()

if __name__ == '__main__':
    app.run()
