import {createRouter, createWebHistory} from 'vue-router'
import FeedView from "@/views/FeedView.vue";
import WatchLaterView from "@/views/WatchLaterView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'feed',
      component: FeedView
    },
    {
      path: '/watch-later',
      name: 'watch-later',
      component: WatchLaterView
    }
  ]
})

export default router
