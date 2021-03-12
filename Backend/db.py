from neo4j import GraphDatabase
import uuid

class db:
    def __init__(self, connection_string: str, auth: tuple):
        self._db_driver = GraphDatabase.driver(connection_string, auth=auth)

    """RETURNS: singular food entity corresponding to the id or none"""
    def find_food_item_by_id(self, id:str):
        query = 'MATCH(f:food) WHERE f.id = $id RETURN f'
        with self._db_driver.session() as session:
            result = session.run(query, id = id).single()

            if result:
                return result.data()['f']
            else:
                None

    """RETURNS: None
       CREATES: New food item"""
    def create_food_tem(self, food_id, item_name, calories, carbs, fat, protein):
        query = 'CREATE (f:food {id: $id, name: $name, calories: $calories, carbohydrates: $carbohydrates,  fat: $fat, protein: $protein})'
        with self._db_driver.session() as session:
            session.run(query, id = food_id, name = item_name, calories = calories, carbohydrates = carbs, fat = fat, protein = protein)
            
    """RETURNS: list of food items similar to those the inputted user ate at the inputted meal"""
    def recommend_foods(self, user_id, meal):
        query = (" ".join([
        f"MATCH (u:user {{id: '{user_id}'}})-[breakfast:ATE {{meal: '{meal}'}}]->(breakfastFood:food)",
        f"MATCH (u:user {{id: '{user_id}'}})-[lunch:ATE {{meal: '{meal}'}}]->(lunchFood:food)",
        f"MATCH (u:user {{id: '{user_id}'}})-[dinner:ATE {{meal: '{meal}'}}]->(dinnerFood:food)",
        "WHERE breakfast.time = lunch.time = dinner.time",
        "AND breakfastFood.calories + lunchFood.calories + dinnerFood.calories < u.calorie_goal",
        f"MATCH ({meal}Food)-[similarity:SIMILAR]->(resultFood:food)",
        "WHERE similarity.score > 0",
        "RETURN collect(resultFood) AS recommendations"
    ]))

        with self._db_driver.session() as session:
            result = session.run(query, id=user_id, meal=meal)
            record = result.single()
            response = record.data()

        return response
            
    """RETURNS: list of food items eaten by a provided user"""
    def find_food_eaten_by_user_on_date(self, user_id: str, year: str, month: str, day: str):
        date_string = f"{year}-{month}-{day}"
        all_food_eaten = (f"MATCH (u:user)-[r:ATE]-(f:food) WHERE r.time = '{date_string}' AND u.id = '{user_id}' WITH {{food: f, meal: r.meal}} AS data RETURN collect(data) AS results")

        # Above query (beautified):
        # MATCH (u: user), (f: food)
        # WHERE u.id = {user_id} AND (u)-[:ATE]->(f)
        # RETURN collect(f)

        with self._db_driver.session() as session:
            result = session.run(all_food_eaten)
            record = result.single()
            response = record.data()

        return response
    
    """RETURNS: None
       CREATES: New ATE relationship between provided user and food"""
    def create_ate_relationship(self, user_id: str, food_id: str, meal_type: str, year: str, month: str, day: str):
        time_string = f"{year}-{month}-{day}"
        new_food_eaten = (f"MATCH (u: user), (f: food) WHERE u.id = '{user_id}' AND f.id = '{food_id}' CREATE (u)-[:ATE {{time: '{time_string}', meal: '{meal_type}'}}]->(f)")

        # Above query (beautified):
        # MATCH (u: user), (f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # CREATE (u)-[:ATE {{timestamp: {eat_time}, meal: {meal_type}}}]->(f)

        with self._db_driver.session() as session:
            session.run(new_food_eaten)
    
    """RETURNS: list of food items liked by a provided user"""
    # UNUSED
    def find_food_liked_by_user(self, user_id: str):
        all_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = '{user_id}' AND (u)-[:LIKES]->(f) RETURN collect(f)")

        # Above query (beautified):
        # MATCH (u: user), (f: food)
        # WHERE u.id = {user_id} AND (u)-[:LIKES]->(f)
        # RETURN collect(f)

        with self._db_driver.session() as session:
            result = session.run(all_food_liked)
            record = result.single()
            response = record.data()

        return response
    
    """RETURNS: None
       CREATES: New LIKES relationship between provided user and food"""
    # UNUSED
    def create_likes_relationship(self, user_id: str, food_id: str):
        new_food_liked = (f"MATCH (u: user), (f: food) WHERE u.id = '{user_id}' AND f.id = '{food_id}' CREATE (u)-[:LIKES]->(f)")

        # Above query (beautified):
        # MATCH (u: user), (f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # CREATE (u)-[:LIKES]->(f)

        with self._db_driver.session() as session:
            session.run(new_food_liked)
    
    """RETURNS: None
       DELETES: An existing LIKES relationship between provided user and food"""
    # UNUSED
    def delete_likes_relationship(self, user_id: str, food_id: str):
        food_deleted = (f"MATCH (u: user)-[r:LIKES]->(f: food) WHERE u.id = '{user_id}' AND f.id = '{food_id}' DELETE r")

        # Above query (beautified):
        # MATCH (u: user)-[r:LIKES]->(f: food)
        # WHERE u.id = {user_id} AND f.id = {food_id}
        # DELETE r

        with self._db_driver.session() as session:
            session.run(food_deleted)

    def find_food_items_by_text_search(self, search_query):
        query = "MATCH (f:food) WHERE "

        words = search_query.split(" ")
        contains_expressions = [f"f.name CONTAINS '{word}' " for word in words]

        query += "AND ".join(contains_expressions)
        query += "RETURN collect(f) AS foods"

        with self._db_driver.session() as session:
            result = session.run(query)
            record = result.single()

            if record:
                return record.data()
            else:
                return None

    def get_user_by_id(self, user_id):
        query = 'MATCH(u:user) WHERE u.id = $id RETURN u'
        with self._db_driver.session() as session:
            result = session.run(query, id = user_id).single()

            if result:
                return result.data()['u']
            else:
                None

    def create_user(self, new_user):
        query = 'CREATE (u:user { id: $id , name: $name , age: $age , weight: $weight , calorie_goal: $calorie_goal })'
        uid = uuid.uuid1()
        with self._db_driver.session() as session:
            session.run(query, id = str(uid), name = new_user['name'], age = new_user['age'], weight = new_user['weight'], calorie_goal = new_user['calorie_goal'])

        return str(uid)
