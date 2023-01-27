package DTO;

import Entities.Brand;
import Entities.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListDTO {


    private List<Brand> listOfBrands = new ArrayList<>();
    private List<Model> listOfModels = new ArrayList<>();


    public ListDTO() {
    }

    public ListDTO(List<Brand> listOfBrands, List<Model> listOfModels) {
        this.listOfBrands = listOfBrands;
        this.listOfModels = listOfModels;
    }


    public List<Brand> getListOfBrands() {
        return listOfBrands;
    }

    public void setListOfBrands(List<Brand> listOfBrands) {
        this.listOfBrands = listOfBrands;
    }

    public List<Model> getListOfModels() {
        return listOfModels;
    }

    public void setListOfModels(List<Model> listOfModels) {
        this.listOfModels = listOfModels;
    }
}

