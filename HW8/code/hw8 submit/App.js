import React from 'react';
import './App.css';
import NavigationBar from "./components/NavigationBar/NavigationBar";
import DefaultArticle from "./components/DefaultArticle/DefaultArticle";
import DetailedArticle from "./components/DetailedArticle/DetailedArticle";
import ResultArticle from "./components/ResultArticle/ResultArticle";
import FavoriteArticle from "./components/FavoriteArticle/FavoriteArticle";
import NavigationPage from "./components/NavigationPage/NavigationPage";
import { toast } from 'react-toastify';

toast.configure({
    autoClose: 8000,
    draggable: false,
    hideProgressBar: true,
    pauseOnHover: false,
    style:{
        width: 400
    }
});

function App() {
    if (localStorage.getItem('source') == null) {
        localStorage.setItem('source', 'NYTimes');
    }
    if (localStorage.getItem('checked') == null) {
        localStorage.setItem('checked', 'false');
    }
    if (localStorage.getItem('favorites') == null) {
        localStorage.setItem('favorites', '{}');
    }

    return (
        <div>
            <NavigationPage />
        </div>
    );
}

export default App;
