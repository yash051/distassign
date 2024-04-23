from mrjob.job import MRJob
from mrjob.step import MRStep
import csv
from datetime import datetime


class CalculateGrades(MRJob):

    def steps(self):
        return [
            MRStep(mapper=self.mapper,
                   reducer=self.reducer)
        ]

    def mapper(self, _, line):
        if line.startswith('date'):
            return
          
        temp = {}

        reader = csv.reader([line])
        for row in reader:
            date, tmx, tmn = str(row[0]), float(row[2]), float(row[3])
            temp[date] = (tmx, tmn)  # Store both max and min temperatures
        yield _, temp

    def reducer(self, key, values):
        # Convert the generator to a list to access elements multiple times
        temps = list(values)
        
        # Find the hottest year
        max_pair = max(temps, key=lambda x: list(x.values())[0][0])
        max_pair_key = list(max_pair.keys())[0]
        max_pair_value_max = max_pair[max_pair_key][0]  # Maximum temperature

        # Find the coolest year
        min_pair = min(temps, key=lambda x: list(x.values())[0][1])
        min_pair_key = list(min_pair.keys())[0]
        min_pair_value_min = min_pair[min_pair_key][1]  # Minimum temperature

        yield None, (max_pair_key, max_pair_value_max, min_pair_key, min_pair_value_min)

    def reducer_final(self, _, values):
        # Unpack the single tuple yielded by the reducer into individual values
        max_year, max_temp, min_year, min_temp = values
        yield max_year, max_temp, min_year,  min_temp


if __name__ == "__main__":
    CalculateGrades.run()